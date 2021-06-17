package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.appizona.yehiahd.fastsave.FastSave
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
import com.dellainfotech.smartTouch.common.utils.FileHelper.getRealPathFromUri
import com.dellainfotech.smartTouch.common.utils.Utils.toBoolean
import com.dellainfotech.smartTouch.common.utils.Utils.toInt
import com.dellainfotech.smartTouch.databinding.FragmentDeviceCustomizationBinding
import com.dellainfotech.smartTouch.mqtt.*
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Jignesh Dangar on 22-04-2021.
 */

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
                        }else -> {
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
        mqttConnectionDisposable?.dispose()
    }

    private fun clickEvents() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ibLock.setOnClickListener {
            activity?.let {
                val msg: String
                if (isDeviceCustomizationLocked) {
                    isDeviceCustomizationLocked = false
                    msg = getString(R.string.dialog_title_text_unlock)
                } else {
                    isDeviceCustomizationLocked = true
                    msg = getString(R.string.dialog_title_text_lock)
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
                                    isDeviceCustomizationLocked.toInt()
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

        binding.layoutTextColor.colorPicker.setColorListener { color, string ->
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

            if (mProfileFile == null) {
                context?.let { mContext ->
                    Toast.makeText(mContext, "Please select image.", Toast.LENGTH_SHORT).show()
                }
            } else {
                imageParts.clear()

                activity?.let {
                    DialogUtil.loadingAlert(it)
                }

                Log.e(logTag, " mProfileFile $mProfileFile ")
                Log.e(logTag, " imagePath $imagePath ")
                Log.e(logTag, " imageName $imageName ")

                val fileExtension = mProfileFile!!.extension

                imageParts.add(
                    MultipartBody.Part.createFormData(
                        "image", imageName,
                        mProfileFile!!.asRequestBody("image/$fileExtension".toMediaTypeOrNull())
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
                Log.e(logTag, " payload $payload")
                if (AwsMqttSingleton.isConnected()) {
                    publish(payload.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
                try {
//                    mPhotoFile = mCompressor.compressToFile(mPhotoFile)
                    Log.e(logTag, " camera mProfileFile $mProfileFile ")
                    Log.e(logTag, " camera imagePath $imagePath ")
                    Log.e(logTag, " camera imageName $imageName ")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else if (requestCode == Constants.REQUEST_GALLERY_IMAGE) {
                val selectedImage: Uri? = data?.data
                try {
                    selectedImage?.let { uri ->
                        getRealPathFromUri(uri)?.let {
                            mProfileFile = File(it)
                            imagePath = mProfileFile!!.absolutePath
                            imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)

                            Log.e(logTag, " mProfileFile $mProfileFile ")
                            Log.e(logTag, " imagePath $imagePath ")
                            Log.e(logTag, " imageName $imageName ")

                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
                            binding.spinnerIconSize.setSelection(sizeAdapter?.getPosition(it.switchIconSize)!!)
                            binding.cbSwitchNameSettings.isChecked =
                                it.switchName.toInt().toBoolean()
                            binding.spinnerTextSize.setSelection(sizeAdapter?.getPosition(it.textSize)!!)
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
                        response.values.data?.let {
                            isDeviceCustomizationLocked = it.isLock.toBoolean()
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
                                showSettingsDialog();
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
            builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
                dialog.cancel()
                openSettings()
            }
            builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
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

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        context?.let {
            if (takePictureIntent.resolveActivity(it.packageManager) != null) {
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
                        it, BuildConfig.APPLICATION_ID + ".provider",
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
                            val deviceStatus = jsonObject.getInt(MQTTConstants.AWS_STATUS)
                            if (deviceStatus == 1) {
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