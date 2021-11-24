package com.voinismartiot.voni.ui.fragments.main.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.appizona.yehiahd.fastsave.FastSave
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.voinismartiot.voni.BuildConfig
import com.voinismartiot.voni.R
import com.voinismartiot.voni.adapters.RoomsAdapter
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyLogout
import com.voinismartiot.voni.api.model.GetRoomData
import com.voinismartiot.voni.api.repository.HomeRepository
import com.voinismartiot.voni.common.interfaces.AdapterItemClickListener
import com.voinismartiot.voni.common.interfaces.DialogAskListener
import com.voinismartiot.voni.common.interfaces.DialogShowListener
import com.voinismartiot.voni.common.utils.Constants
import com.voinismartiot.voni.common.utils.DialogUtil
import com.voinismartiot.voni.common.utils.Utils.toBoolean
import com.voinismartiot.voni.common.utils.showToast
import com.voinismartiot.voni.databinding.FragmentHomeBinding
import com.voinismartiot.voni.mqtt.NotifyManager
import com.voinismartiot.voni.ui.activities.AuthenticationActivity
import com.voinismartiot.voni.ui.activities.MainActivity
import com.voinismartiot.voni.ui.fragments.ModelBaseFragment
import com.voinismartiot.voni.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class HomeFragment : ModelBaseFragment<HomeViewModel, FragmentHomeBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private lateinit var roomsAdapter: RoomsAdapter
    private var roomList = arrayListOf<GetRoomData>()
    private var mGoogleSingInClient: GoogleSignInClient? = null
    private var roomData: GetRoomData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivSideNavigation.setOnClickListener {
            openOrCloseDrawer()
        }

        val headerView: View = binding.sideNavigationView.getHeaderView(0)
        val navUsername = headerView.findViewById(R.id.tv_user_name) as TextView
        val navUserEmail = headerView.findViewById(R.id.tv_user_email) as TextView

        navUsername.text = FastSave.getInstance().getString(Constants.USER_FULL_NAME, null)
        navUserEmail.text = FastSave.getInstance().getString(Constants.USER_EMAIL, null)

        roomsAdapter = RoomsAdapter(roomList)
        binding.recyclerRooms.adapter = roomsAdapter
        roomsAdapter.setCallback(object : AdapterItemClickListener<GetRoomData> {
            override fun onItemClick(data: GetRoomData) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToRoomPanelFragment(
                        data
                    )
                )
            }
        })

        roomsAdapter.setDeleteCallback(object : AdapterItemClickListener<GetRoomData> {
            override fun onItemClick(data: GetRoomData) {
                activity?.let {
                    DialogUtil.askAlert(
                        it,
                        getString(R.string.dialog_title_delete_room),
                        getString(R.string.text_ok),
                        getString(R.string.text_cancel),
                        object : DialogAskListener {
                            override fun onYesClicked() {
                                DialogUtil.loadingAlert(it)
                                roomData = data
                                viewModel.deleteRoom(data.id)
                            }

                            override fun onNoClicked() {

                            }

                        }
                    )
                }
            }

        })

        binding.tvAppVersion.text = String.format("%s", "Version - ${BuildConfig.VERSION_NAME}")

        initGoogleSignInClient()

        // initializing navigation menu
        setUpNavigationView()

        if (!isInternetConnected()) {
            activity?.let {
                DialogUtil.deviceOfflineAlert(
                    it,
                    getString(R.string.text_no_internet_available),
                    object : DialogShowListener {
                        override fun onClick() {
                            DialogUtil.hideDialog()
                        }

                    }
                )
            }
        }

        apiCall()
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

    //Initialization object of GoogleSignInClient
    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSingInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun apiCall() {

        NotifyManager.internetInfo.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                roomList.toMutableList().clear()
                activity?.let {
                    DialogUtil.loadingAlert(it)
                }
                viewModel.getRoom()
                viewModel.getPinStatus()
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.logoutResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    activity?.let {

                                        val sharedPreference = it.getSharedPreferences(
                                            Constants.SHARED_PREF,
                                            Context.MODE_PRIVATE
                                        )
                                        val isRemember = sharedPreference.getBoolean(
                                            Constants.IS_REMEMBER,
                                            Constants.DEFAULT_REMEMBER_STATUS
                                        )
                                        val loginType =
                                            sharedPreference.getString(Constants.LOGGED_IN_TYPE, "")

                                        if (loginType == Constants.LOGIN_TYPE_NORMAL) {
                                            if (!isRemember) {
                                                val editor = sharedPreference.edit()
                                                editor.clear()
                                                editor.apply()
                                            }
                                        } else {
                                            val editor = sharedPreference.edit()
                                            editor.clear()
                                            editor.apply()
                                        }

                                        FastSave.getInstance().clearSession()
                                        startActivity(
                                            Intent(
                                                it,
                                                AuthenticationActivity::class.java
                                            )
                                        )
                                        it.finishAffinity()
                                    }
                                } else {
                                    context?.showToast(response.values.message)
                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(logTag, "logout error ${response.errorBody?.string()}")
                            }
                            else -> {
                                // We will do nothing here
                            }
                        }
                    }
                }

                launch {
                    viewModel.getRoomResponse.collectLatest { response ->
                        roomList.clear()
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    response.values.data?.let { roomData ->
                                        roomList.addAll(roomData)
                                        roomsAdapter.notifyDataSetChanged()
                                    }
                                } else {
                                    roomsAdapter.notifyDataSetChanged()
                                    context?.showToast(response.values.message)
                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    "getRoomResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> {
                                // We will do nothing here
                            }
                        }
                    }
                }

                launch {
                    viewModel.deleteRoomResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                context?.showToast(response.values.message)

                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    roomData?.let {
                                        roomList.remove(it)
                                        roomsAdapter.notifyDataSetChanged()
                                        roomData = null
                                    }
                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " deleteRoomResponse Failure ${response.errorBody?.string()} "
                                )
                            }
                            else -> {
                                //We will do nothing here
                            }
                        }
                    }
                }

                launch {
                    viewModel.factoryResetAllDeviceResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                context?.showToast(response.values.message)
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(
                                    logTag,
                                    " profileResetResponse Failure ${response.errorBody?.string()}"
                                )
                            }
                            else -> {
                                //We will do nothing here
                            }
                        }
                    }
                }

                launch {
                    viewModel.getPinStatusResponse.collectLatest { response ->
                        when (response) {
                            is Resource.Success -> {
                                DialogUtil.hideDialog()
                                if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                                    response.values.data?.let {
                                        FastSave.getInstance().saveBoolean(
                                            Constants.isControlModePinned,
                                            it.isPinStatus.toBoolean()
                                        )

                                        if (it.isPinStatus.toBoolean()) {
                                            pinnedControlMode()
                                        } else {
                                            unpinnedControlMode()
                                        }
                                    }
                                }
                            }
                            is Resource.Failure -> {
                                DialogUtil.hideDialog()
                                context?.showToast(getString(R.string.error_something_went_wrong))
                                Log.e(logTag, " updatePinStatusResponse Failure $response ")
                            }
                            else -> {
                                //We will do nothing here
                            }
                        }
                    }
                }

            }
        }

    }

    private fun pinnedControlMode() {
        (activity as MainActivity).hideBottomNavigation()
    }

    private fun unpinnedControlMode() {
        (activity as MainActivity).showBottomNavigation()
    }

    private fun openOrCloseDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun setUpNavigationView() {
        binding.sideNavigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_account_settings -> {
                    openOrCloseDrawer()
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAccountSettingsFragment())
                }
                R.id.nav_restore_devices -> {
                    activity?.let {
                        DialogUtil.askAlert(
                            it,
                            getString(R.string.dialog_title_restore_device),
                            getString(R.string.text_ok),
                            getString(R.string.text_cancel),
                            object : DialogAskListener {
                                override fun onYesClicked() {
                                    openOrCloseDrawer()
                                    DialogUtil.loadingAlert(it)
                                    viewModel.factoryResetAllDevice()
                                }

                                override fun onNoClicked() {

                                }

                            }
                        )
                    }
                }
                R.id.nav_profile_reset -> {
                    activity?.let {
                        DialogUtil.askAlert(
                            it,
                            getString(R.string.dialog_title_profile_reset),
                            getString(R.string.text_ok),
                            getString(R.string.text_cancel)
                        )
                    }
                }
                R.id.nav_faqs -> {
                    openOrCloseDrawer()
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFaqsFragment())
                }
                R.id.nav_shop -> {
                    Log.e(logTag, "nav_shop")
                }
                R.id.nav_contact_us -> {
                    openOrCloseDrawer()
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToContactUsFragment())
                }
                R.id.nav_logout -> {

                    activity?.let { mActivity ->
                        DialogUtil.askAlert(
                            mActivity,
                            getString(R.string.dialog_title_logout),
                            getString(R.string.text_yes),
                            getString(R.string.text_no),
                            object : DialogAskListener {
                                override fun onYesClicked() {
                                    DialogUtil.hideDialog()
                                    val loginType =
                                        FastSave.getInstance().getString(Constants.LOGIN_TYPE, "")
                                    if (loginType == Constants.LOGIN_TYPE_GOOGLE) {
                                        mGoogleSingInClient?.signOut()
                                    } else if (loginType == Constants.LOGIN_TYPE_FACEBOOK) {
                                        LoginManager.getInstance().logOut()
                                    }

                                    DialogUtil.loadingAlert(mActivity)

                                    viewModel.logout(
                                        BodyLogout(
                                            FastSave.getInstance()
                                                .getString(Constants.MOBILE_UUID, null)
                                        )
                                    )
                                }

                                override fun onNoClicked() {
                                    DialogUtil.hideDialog()
                                }

                            })
                    }

                }
            }

            true
        }
    }

}