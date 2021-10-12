package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.appizona.yehiahd.fastsave.FastSave
import com.canhub.cropper.CropImageView
import com.dellainfotech.smartTouch.BuildConfig
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.spinneradapter.SpinnerAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyCustomizationLock
import com.dellainfotech.smartTouch.api.model.DeviceCustomizationData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.interfaces.DialogShowListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.common.utils.FileHelper.getImageOrientation
import com.dellainfotech.smartTouch.common.utils.FileHelper.getRealPathFromUri
import com.dellainfotech.smartTouch.common.utils.FileHelper.sizeInMb
import com.dellainfotech.smartTouch.common.utils.Utils.getImageUri
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.dellainfotech.smartTouch.common.utils.Utils.toInt
import com.dellainfotech.smartTouch.common.utils.Utils.toReverseInt
import com.dellainfotech.smartTouch.databinding.FragmentDeviceCustomizationBinding
import com.dellainfotech.smartTouch.mqtt.AwsMqttSingleton
import com.dellainfotech.smartTouch.mqtt.MQTTConnectionStatus
import com.dellainfotech.smartTouch.mqtt.MQTTConstants
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.google.android.material.button.MaterialButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Jignesh Dangar on 22-04-2021.
 */

@Suppress("DEPRECATION")
class DeviceCustomizationFragment :
    ModelBaseFragment<HomeViewModel, FragmentDeviceCustomizationBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: DeviceCustomizationFragmentArgs by navArgs()
    private lateinit var sizeAdapter: SpinnerAdapter
    private lateinit var textStyleAdapter: SpinnerAdapter
    private var deviceCustomization: DeviceCustomizationData? = null
    private var isDeviceCustomizationLocked: Boolean = false

    private var mqttConnectionDisposable: Disposable? = null

    private var imageParts: MutableList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
    private var imagePath = ""
    private var imageName = ""
    private var mProfileFile: File? = null
    private var mCroppedImageFile: File? = null

    private var dialogCropImage: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sizeList = arrayOf("Small", "Medium", "Large")
        val fontNames = arrayOf("Times New Roman", "Arial", "Roman")

        if (FastSave.getInstance().getBoolean(
                Constants.isDeviceCustomizationLocked,
                Constants.DEFAULT_DEVICE_CUSTOMIZATION_LOCK_STATUS
            )
        ) {
            lockScreen()
        }

        mqttConnectionDisposable =
            NotifyManager.getMQTTConnectionInfo().observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.e(logTag, " MQTTConnectionStatus = $it ")
                    when (it) {
                        MQTTConnectionStatus.CONNECTED -> {
                            subscribeToDevice(args.deviceDetail.deviceSerialNo)
                        }
                        else -> {
                            //We will do nothing here
                        }
                    }
                }

        activity?.let { mActivity ->

            sizeAdapter = SpinnerAdapter(mActivity, sizeList.toMutableList())
            binding.spinnerIconSize.adapter = sizeAdapter
            binding.spinnerTextSize.adapter = sizeAdapter

            textStyleAdapter = SpinnerAdapter(mActivity, fontNames.toMutableList())
            binding.layoutTextStyle.spinnerFonts.adapter = textStyleAdapter

        }

        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                activity?.let {
                    DialogUtil.loadingAlert(it)
                }
                viewModel.getDeviceCustomization(args.deviceDetail.id)
            } else {
                activity?.let {
                    DialogUtil.deviceOfflineAlert(
                        it,
                        getString(R.string.text_no_internet_available),
                        object : DialogShowListener {
                            override fun onClick() {
                                DialogUtil.hideDialog()
                                findNavController().navigate(DeviceCustomizationFragmentDirections.actionGlobalHomeFragment())
                            }

                        }
                    )
                }
            }
        })

        clickEvents()

        apiCall()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.customizationLockResponse.postValue(null)
        viewModel.imageUploadResponse.postValue(null)
        viewModel.deleteImageResponse.postValue(null)
        mqttConnectionDisposable?.dispose()
    }

    private fun clickEvents() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.layoutSlidingUpPanel.setFadeOnClickListener { hidePanel() }

        binding.ibLock.setOnClickListener {
            activity?.let {
                val msg = if (isDeviceCustomizationLocked) {
                    getString(R.string.dialog_title_text_unlock)
                } else {
                    getString(R.string.dialog_title_text_lock)
                }

                DialogUtil.askAlert(
                    it,
                    msg,
                    getString(R.string.text_ok),
                    getString(R.string.text_cancel),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            DialogUtil.loadingAlert(it)
                            viewModel.customizationLock(
                                BodyCustomizationLock(
                                    args.deviceDetail.id,
                                    isDeviceCustomizationLocked.toReverseInt()
                                )
                            )
                        }

                        override fun onNoClicked() {

                        }

                    }
                )
            }
        }

        binding.ivScreenLayoutSettings.setOnClickListener {
            deviceCustomization?.let {
                findNavController().navigate(
                    DeviceCustomizationFragmentDirections.actionDeviceCustomizationFragmentToScreenLayoutFragment(
                        it, args.deviceDetail, args.roomDetail
                    )
                )
            }
        }

        binding.ivUploadImageSettings.setOnClickListener {
            context?.let { mContext ->
                binding.tvBottomViewTitle.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.theme_color
                    )
                )
            }
            binding.tvBottomViewTitle.text = getString(R.string.text_upload_image)
            binding.layoutTextStyle.linearTextStyle.isVisible = false
            binding.layoutTextColor.linearTextColor.isVisible = false
            binding.layoutUploadImage.linearUploadImage.isVisible = true

            showPanel()
        }

        binding.ivHideUploadImagePanel.setOnClickListener {
            hidePanel()
        }

        binding.layoutUploadImage.ivPhoto.setOnClickListener {
            checkPermission(true)
        }

        binding.layoutUploadImage.ivGallery.setOnClickListener {
            checkPermission(false)
        }

        binding.ivTextStyleSettings.setOnClickListener {
            binding.tvBottomViewTitle.text = getString(R.string.text_style)
            context?.let { mContext ->
                binding.tvBottomViewTitle.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.theme_color
                    )
                )
            }
            binding.layoutUploadImage.linearUploadImage.isVisible = false
            binding.layoutTextColor.linearTextColor.isVisible = false
            binding.layoutTextStyle.linearTextStyle.isVisible = true

            val pos = textStyleAdapter.getPosition(deviceCustomization?.textStyle)
            binding.layoutTextStyle.spinnerFonts.setSelection(pos)

            showPanel()
        }

        binding.ivTextColorSettings.setOnClickListener {
            binding.layoutTextColor.colorPicker.setColor(Color.parseColor(deviceCustomization?.textColor))
            binding.tvBottomViewTitle.text = getString(R.string.text_color)
            binding.layoutUploadImage.linearUploadImage.isVisible = false
            binding.layoutTextStyle.linearTextStyle.isVisible = false
            binding.layoutTextColor.linearTextColor.isVisible = true
            showPanel()
        }

        binding.layoutTextColor.colorPicker.setColorListener { color, _ ->
            binding.tvBottomViewTitle.setTextColor(color)
        }

        binding.layoutTextColor.btnColorPickerDone.setOnClickListener {
            deviceCustomization?.textColor = java.lang.String.format(
                "#%06X",
                0xFFFFFF and binding.layoutTextColor.colorPicker.getColor()
            )
            hidePanel()
        }

        binding.ivSwitchIconsSettings.setOnClickListener {
            findNavController().navigate(
                DeviceCustomizationFragmentDirections.actionDeviceCustomizationFragmentToSwitchIconsFragment(
                    args.deviceDetail, args.roomDetail
                )
            )
        }

        binding.layoutUploadImage.linearUploadImage.setOnClickListener {

            activity?.let { mActivity ->

                if (mCroppedImageFile == null) {
                    Toast.makeText(mActivity, "Please select image.", Toast.LENGTH_SHORT).show()
                } else {

                    Log.e(logTag, " mCroppedImageFile size ${mCroppedImageFile!!.sizeInMb} ")

                    mCroppedImageFile?.let { cropFile ->

                        if (cropFile.sizeInMb > 5.0) {
                            Toast.makeText(
                                mActivity,
                                " Image size must be less than 5MB",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            imageParts.clear()

                            DialogUtil.loadingAlert(mActivity)

                            val fileExtension = mCroppedImageFile!!.extension

                            imageParts.add(
                                MultipartBody.Part.createFormData(
                                    "image", imageName,
                                    mCroppedImageFile!!.asRequestBody("image/$fileExtension".toMediaTypeOrNull())
                                )
                            )

                            hidePanel()
                            viewModel.imageUpload(
                                args.deviceDetail.id.toRequestBody("text/plain".toMediaTypeOrNull()),
                                imageParts
                            )

                            imagePath = ""
                            imageName = ""
                            mProfileFile = null
                        }
                    }

                }

            }

        }

        binding.layoutUploadImage.ivRemove.setOnClickListener {

            activity?.let { mActivity ->

                DialogUtil.askAlert(
                    mActivity,
                    getString(R.string.dialog_title_remove_device_image),
                    getString(R.string.text_ok),
                    getString(R.string.text_cancel),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            hidePanel()
                            DialogUtil.loadingAlert(mActivity)
                            viewModel.deleteImage(args.deviceDetail.id)
                        }

                        override fun onNoClicked() {

                        }

                    }
                )
            }
        }

        binding.layoutTextStyle.btnSave.setOnClickListener {
            deviceCustomization?.textStyle =
                binding.layoutTextStyle.spinnerFonts.selectedItem.toString()
            hidePanel()
        }

        binding.btnSynchronize.setOnClickListener {
            try {
                val payload = JSONObject()
                Log.e(logTag, " uploadImage ${deviceCustomization?.uploadImage}")
                payload.put(MQTTConstants.AWS_UPLOAD_IMAGE, deviceCustomization?.uploadImage)
                payload.put(
                    MQTTConstants.AWS_SCREEN_LAYOUT_TYPE,
                    deviceCustomization?.screenLayoutType
                )
                payload.put(MQTTConstants.AWS_SCREEN_LAYOUT, deviceCustomization?.screenLayout)
                payload.put(
                    MQTTConstants.AWS_SWITCH_NAME,
                    binding.cbSwitchNameSettings.isChecked.toInt()
                )
                payload.put(
                    MQTTConstants.AWS_SWITCH_ICON_SIZE,
                    binding.spinnerIconSize.selectedItem
                )
                payload.put(MQTTConstants.AWS_TEXT_STYLE, deviceCustomization?.textStyle)
                payload.put(MQTTConstants.AWS_TEXT_COLOR, deviceCustomization?.textColor)
                payload.put(MQTTConstants.AWS_TEXT_SIZE, binding.spinnerTextSize.selectedItem)
                payload.put(
                    MQTTConstants.AWS_CUSTOMIZATION_LOCK,
                    isDeviceCustomizationLocked.toInt()
                )
                val switchIconsArray = JSONArray()
                args.deviceDetail.switchData?.let {
                    for (switch in it) {
                        if (switch.typeOfSwitch == 0) {
                            val switchIconsObject = JSONObject()
                            switchIconsObject.put("SW0${switch.index.toInt()}", switch.iconFile)
                            switchIconsArray.put(switchIconsObject)
                        }
                    }
                }
                payload.put(MQTTConstants.AWS_SWITCH_ICONS, switchIconsArray)

                Log.e(logTag, " payload $payload")
                if (AwsMqttSingleton.isConnected()) {
                    publish(payload.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.ivScreenLayoutInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_screen_layout),
                    getString(R.string.description_screen_layout)
                )
            }
        }

        binding.ivUploadImageInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_upload_image),
                    getString(R.string.description_upload_image)
                )
            }
        }

        binding.ivSwitchIconsInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_switch_icons),
                    getString(R.string.description_switch_icons)
                )
            }
        }

        binding.ivSwitchIconSizeInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_switch_icon_size),
                    getString(R.string.description_switch_icons_size)
                )
            }
        }

        binding.ivSwitchNameInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_switch_name),
                    getString(R.string.description_switch_name)
                )
            }
        }

        binding.ivTextStyleInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_style),
                    getString(R.string.description_text_style)
                )
            }
        }

        binding.ivTextColorInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_color),
                    getString(R.string.description_text_color)
                )
            }
        }

        binding.ivTextSizeInfo.setOnClickListener {
            activity?.let { mActivity ->
                DialogUtil.featureDetailAlert(
                    mActivity,
                    getString(R.string.text_size),
                    getString(R.string.description_text_size)
                )
            }
        }
    }

    private fun showPanel() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }, 600)
    }

    private fun hidePanel() {
        binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeviceCustomizationBinding =
        FragmentDeviceCustomizationBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK) {
            val selectedImage: Uri? = data?.data
            try {
                selectedImage?.let { uri ->
                    getRealPathFromUri(uri)?.let {
                        mProfileFile = File(it)
                        imagePath = mProfileFile!!.absolutePath
                        imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
                    }

                    val bitMapOption = BitmapFactory.Options()
                    bitMapOption.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(imagePath, bitMapOption)
                    val imageWidth = bitMapOption.outWidth
                    val imageHeight = bitMapOption.outHeight

                    Log.e(logTag, " imageWidth $imageWidth imageHeight $imageHeight ")

                    dialogCropImage(uri)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            val bitMapOption = BitmapFactory.Options()
            bitMapOption.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, bitMapOption)
            val imageWidth = bitMapOption.outWidth
            val imageHeight = bitMapOption.outHeight

            Log.e(logTag, " imageWidth $imageWidth imageHeight $imageHeight ")

            mProfileFile?.let { mFile ->
                dialogCropImage(mFile.toUri())
            }
        }

    }

    private fun apiCall() {
        viewModel.getDeviceCustomizationSettingsResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()

                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let {
                            deviceCustomization = it
                            binding.spinnerIconSize.setSelection(sizeAdapter.getPosition(it.switchIconSize))
                            binding.cbSwitchNameSettings.isChecked =
                                it.switchName.toInt().toBoolean()
                            binding.spinnerTextSize.setSelection(sizeAdapter.getPosition(it.textSize))
                            isDeviceCustomizationLocked = it.isLock.toBoolean()
                            binding.layoutTextColor.colorPicker.setColor(Color.parseColor(it.textColor))
                            if (isDeviceCustomizationLocked) {
                                lockScreen()
                            } else {
                                unLockScreen()
                            }
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                }
                else -> {
                    // We will do nothing here
                }
            }
        })

        viewModel.customizationLockResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {

                        isDeviceCustomizationLocked = !isDeviceCustomizationLocked
                        if (isDeviceCustomizationLocked) {
                            lockScreen()
                        } else {
                            unLockScreen()
                        }

                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(
                        logTag,
                        " customizationLockResponse Failure ${response.errorBody?.string()}"
                    )
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.imageUploadResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()

                    Log.e(
                        logTag,
                        " mCroppedImageFile?.exists() == true ${mCroppedImageFile?.exists()} "
                    )

                    if (mCroppedImageFile?.exists() == true) {
                        mCroppedImageFile?.delete()
                    }
                    mCroppedImageFile = null

                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }

                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let {
                            deviceCustomization?.uploadImage = it.uploadImage
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, "imageUploadResponse Failure ${response.errorBody?.string()}")

                    Log.e(logTag, " mCroppedImageFile?.exists() ${mCroppedImageFile?.exists()} ")
                    if (mCroppedImageFile?.exists() == true) {
                        Log.e(
                            logTag,
                            " mCroppedImageFile?.exists() == true ${mCroppedImageFile?.exists()} "
                        )
                        mCroppedImageFile?.delete()
                    }
                    mCroppedImageFile = null
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.deleteImageResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        deviceCustomization?.uploadImage = ""
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                }
                else -> {
                    //We will do nothing here
                }
            }
        })
    }

    //
    //region ImageSettings
    //

    private fun checkPermission(isCamera: Boolean) {
        activity?.let {

            Dexter.withActivity(it)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let { rep ->
                            if (rep.areAllPermissionsGranted()) {
                                if (isCamera) {
                                    dispatchTakePictureIntent()
                                } else {
                                    dispatchGalleryIntent()
                                }
                            }
                            // check for permanent denial of any permission
                            if (rep.isAnyPermissionPermanentlyDenied) {
                                // show alert dialog navigating to Settings
                                showSettingsDialog()
                            }
                        }

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }

                })
                .withErrorListener { error ->
                    Log.e(logTag, " error $error")
                    Toast.makeText(it, " Error occurred! ", Toast.LENGTH_SHORT).show()
                }
                .onSameThread()
                .check()
        }
    }

    private fun showSettingsDialog() {
        activity?.let {
            val builder: AlertDialog.Builder = AlertDialog.Builder(it)
            builder.setTitle("Need Permissions")
            builder.setMessage(
                "This app needs permission to use this feature. You can grant them in app settings."
            )
            builder.setPositiveButton("GOTO SETTINGS") { dialog, _ ->
                dialog.cancel()
                openSettings()
            }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            builder.show()
        }

    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", context?.packageName, null)
        intent.data = uri
        startActivityForResult(intent, Constants.REQUEST_OPEN_SETTINGS)
    }

    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
        val mFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(mFileName, ".jpg", storageDir)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        context?.let { mContext ->
            if (takePictureIntent.resolveActivity(mContext.packageManager) != null) {
                // Create the File where the photo should go
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                        mContext, BuildConfig.APPLICATION_ID + ".provider",
                        photoFile
                    )
                    mProfileFile = photoFile
                    imagePath = mProfileFile!!.absolutePath
                    imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE)
                }
            }
        }

    }

    private fun dispatchGalleryIntent() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(pickPhoto, Constants.REQUEST_GALLERY_IMAGE)
    }

    private fun dialogCropImage(imageUri: Uri) {
        activity?.let { mActivity ->
            dialogCropImage = Dialog(mActivity)
            dialogCropImage?.setContentView(R.layout.dialog_crop_image)
            dialogCropImage?.setCancelable(true)

            val cropImageView = dialogCropImage?.findViewById(R.id.crop_image_view) as CropImageView
            val btnCrop = dialogCropImage?.findViewById(R.id.btn_crop) as MaterialButton
            val btnSave = dialogCropImage?.findViewById(R.id.btn_save) as MaterialButton
            val ivBack = dialogCropImage?.findViewById(R.id.iv_back) as ImageView
            val progressBar = dialogCropImage?.findViewById(R.id.progress_bar) as ProgressBar

            progressBar.isVisible = false

            btnSave.isEnabled = false
            btnSave.background =
                ContextCompat.getDrawable(mActivity, R.drawable.gray_background_6dp_corner)

            val originalImage: Bitmap =
                MediaStore.Images.Media.getBitmap(mActivity.contentResolver, imageUri)

            if (originalImage.width > 6000 || originalImage.height > 5000) {

                progressBar.isVisible = true

                val matrix = Matrix()
                matrix.postRotate(getImageOrientation(getRealPathFromUri(imageUri)).toFloat())

                viewModel.viewModelScope.launch(Dispatchers.Main){

                    val scaledBitmap = Bitmap.createScaledBitmap(
                        originalImage,
                        (originalImage.width * 0.6).toInt(),
                        (originalImage.height * 0.6).toInt(),
                        true
                    )

                    val rotatedBitmap = Bitmap.createBitmap(
                        scaledBitmap,
                        0,
                        0,
                        scaledBitmap.width,
                        scaledBitmap.height,
                        matrix,
                        true
                    )

                    Log.e(logTag, " resized width ${rotatedBitmap.width} height ${rotatedBitmap.height}")

                    progressBar.isVisible = false
                    cropImageView.setImageBitmap(rotatedBitmap)
                }

            } else {
                cropImageView.setImageUriAsync(imageUri)
            }

            btnCrop.setOnClickListener {

                val croppedImage: Bitmap? = cropImageView.croppedImage
                cropImageView.setImageBitmap(croppedImage)

                btnSave.isEnabled = true
                btnSave.background = ContextCompat.getDrawable(
                    mActivity,
                    R.drawable.dodger_blue_background_6dp_corner
                )
            }

            btnSave.setOnClickListener {

                val croppedImageUri =
                    getImageUri(mActivity, cropImageView.croppedImage!!, imageName)

                croppedImageUri?.let { uri ->
                    getRealPathFromUri(uri)?.let {
                        mCroppedImageFile = File(it)
                        imagePath = mProfileFile!!.absolutePath
                        imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
                    }

                    dialogCropImage?.dismiss()
                    Toast.makeText(mActivity, "Image is selected", Toast.LENGTH_SHORT).show()
                }

            }

            ivBack.setOnClickListener {
                dialogCropImage?.dismiss()
            }

            val displayMetrics = DisplayMetrics()
            mActivity.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels

            dialogCropImage?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogCropImage?.window?.setLayout(width, height)
            dialogCropImage?.show()
        }

    }

    //
    //endregion
    //

    private fun lockScreen() {
        binding.relativeLock.isVisible = true
        context?.let {
            binding.ibLock.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_lock))
        }
    }

    private fun unLockScreen() {
        binding.relativeLock.isVisible = false
        context?.let {
            binding.ibLock.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_unlock))
        }
    }

    //
    //region MQTT
    //

    private fun subscribeToDevice(deviceId: String) {
        try {

            //Current Device Status Update - Online/Offline
            AwsMqttSingleton.mqttManager!!.subscribeToTopic(
                MQTTConstants.DEVICE_STATUS.replace(MQTTConstants.AWS_DEVICE_ID, deviceId),
                AWSIotMqttQos.QOS0
            ) { topic, data ->
                activity?.let {
                    it.runOnUiThread {

                        val message = String(data, StandardCharsets.UTF_8)
                        Log.d("$logTag ReceivedData", "$topic    $message")

                        val jsonObject = JSONObject(message)

                        if (jsonObject.has(MQTTConstants.AWS_STATUS)) {
                            val deviceStatus = jsonObject.getString(MQTTConstants.AWS_STATUS)
                            if (deviceStatus == "1") {
                                DialogUtil.hideDialog()
                            } else {
                                DialogUtil.deviceOfflineAlert(
                                    it,
                                    onClick = object : DialogShowListener {
                                        override fun onClick() {
                                            findNavController().navigateUp()
                                        }

                                    })
                            }
                        }
                    }
                }
            }

            //Current Device Status Update - Online/Offline
            AwsMqttSingleton.mqttManager!!.subscribeToTopic(
                MQTTConstants.DEVICE_CUSTOMIZATION_ACK.replace(
                    MQTTConstants.AWS_DEVICE_ID,
                    deviceId
                ),
                AWSIotMqttQos.QOS0
            ) { topic, data ->
                activity?.let {

                    it.runOnUiThread {

                        Toast.makeText(
                            it,
                            getString(R.string.toast_text_device_synchronized),
                            Toast.LENGTH_SHORT
                        ).show()

                        val message = String(data, StandardCharsets.UTF_8)
                        Log.d("$logTag ReceivedData", "$topic    $message")

                        try {
                            val jsonObject = JSONObject(message)

                            if (jsonObject.has(MQTTConstants.AWS_UPLOAD_IMAGE)) {
                                deviceCustomization?.uploadImage =
                                    jsonObject.getString(MQTTConstants.AWS_UPLOAD_IMAGE)
                            }
                            if (jsonObject.has(MQTTConstants.AWS_SCREEN_LAYOUT_TYPE)) {
                                deviceCustomization?.screenLayoutType =
                                    jsonObject.getString(MQTTConstants.AWS_SCREEN_LAYOUT_TYPE)
                            }
                            if (jsonObject.has(MQTTConstants.AWS_SCREEN_LAYOUT)) {
                                deviceCustomization?.screenLayout =
                                    jsonObject.getString(MQTTConstants.AWS_SCREEN_LAYOUT)
                            }
                            if (jsonObject.has(MQTTConstants.AWS_SWITCH_NAME)) {
                                deviceCustomization?.switchName =
                                    jsonObject.getInt(MQTTConstants.AWS_SWITCH_NAME).toString()
                            }
                            if (jsonObject.has(MQTTConstants.AWS_SWITCH_ICON_SIZE)) {
                                deviceCustomization?.switchIconSize =
                                    jsonObject.getString(MQTTConstants.AWS_SWITCH_ICON_SIZE)
                            }
                            if (jsonObject.has(MQTTConstants.AWS_TEXT_STYLE)) {
                                deviceCustomization?.textStyle =
                                    jsonObject.getString(MQTTConstants.AWS_TEXT_STYLE)
                            }
                            if (jsonObject.has(MQTTConstants.AWS_TEXT_COLOR)) {
                                deviceCustomization?.textColor =
                                    jsonObject.getString(MQTTConstants.AWS_TEXT_COLOR)
                            }
                            if (jsonObject.has(MQTTConstants.AWS_TEXT_SIZE)) {
                                deviceCustomization?.textSize =
                                    jsonObject.getString(MQTTConstants.AWS_TEXT_SIZE)
                            }
                            if (jsonObject.has(MQTTConstants.AWS_CUSTOMIZATION_LOCK)) {
                                deviceCustomization?.isLock =
                                    jsonObject.getInt(MQTTConstants.AWS_CUSTOMIZATION_LOCK)
                            }

                            deviceCustomization?.let { customization ->
                                binding.spinnerIconSize.setSelection(
                                    sizeAdapter.getPosition(
                                        customization.switchIconSize
                                    )
                                )
                                binding.cbSwitchNameSettings.isChecked =
                                    customization.switchName.toInt().toBoolean()
                                binding.spinnerTextSize.setSelection(
                                    sizeAdapter.getPosition(
                                        customization.textSize
                                    )
                                )
                                isDeviceCustomizationLocked = customization.isLock.toBoolean()
                                binding.layoutTextColor.colorPicker.setColor(
                                    Color.parseColor(
                                        customization.textColor
                                    )
                                )
                                if (isDeviceCustomizationLocked) {
                                    lockScreen()
                                } else {
                                    unLockScreen()
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            }
        } catch (e: Exception) {
            Log.e(logTag, "Subscription error.", e)
        }
    }

    private fun publish(payload: String) {

        AwsMqttSingleton.publish(
            MQTTConstants.UPDATE_DEVICE_CUSTOMIZATION.replace(
                MQTTConstants.AWS_DEVICE_ID,
                args.deviceDetail.deviceSerialNo
            ), payload
        )
    }

    //
    //endregion
    //
}