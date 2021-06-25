package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.BuildConfig
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.RoomsAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyLogout
import com.dellainfotech.smartTouch.api.model.GetRoomData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentHomeBinding
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.activities.AuthenticationActivity
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class HomeFragment : ModelBaseFragment<HomeViewModel, FragmentHomeBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private lateinit var roomsAdapter: RoomsAdapter
    private var roomList = arrayListOf<GetRoomData>()
    private var mGoogleSingInClient: GoogleSignInClient? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivSideNavigation.setOnClickListener {
            openOrCloseDrawer()
        }

        val headerView: View = binding.sideNavigationView.getHeaderView(0)
        val navUsername = headerView.findViewById(R.id.tv_user_name) as TextView
        val navUserEmail = headerView.findViewById(R.id.tv_user_email) as TextView

        val sharedPreference =  activity?.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        val isRemember = sharedPreference?.getBoolean(Constants.IS_REMEMBER,Constants.DEFAULT_REMEMBER_STATUS)

        Log.e(logTag, " isRemember $isRemember ")

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

        roomsAdapter.setDeleteCallback(object : AdapterItemClickListener<GetRoomData>{
            override fun onItemClick(data: GetRoomData) {
                activity?.let {
                    DialogUtil.askAlert(
                        it,
                        getString(R.string.dialog_title_delete_room),
                        getString(R.string.text_ok),
                        getString(R.string.text_cancel),
                        object : DialogAskListener{
                            override fun onYesClicked() {
                                Log.e(logTag, "Yes Clicked")
                            }

                            override fun onNoClicked() {
                                Log.e(logTag, "No Clicked")
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

        apiCall()
    }

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
                viewModel.getRoom()
                activity?.let {
                    DialogUtil.loadingAlert(it)
                }
            }
        })

        viewModel.logoutResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        activity?.let {

                            val sharedPreference =  it.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
                            val isRemember = sharedPreference.getBoolean(Constants.IS_REMEMBER,Constants.DEFAULT_REMEMBER_STATUS)
                            val loginType = sharedPreference.getString(Constants.LOGGED_IN_TYPE, "")

                            if (loginType == Constants.LOGIN_TYPE_NORMAL){
                                if (!isRemember){
                                    val editor = sharedPreference.edit()
                                    editor.clear()
                                    editor.apply()
                                }
                            }else {
                                val editor = sharedPreference.edit()
                                editor.clear()
                                editor.apply()
                            }

                            FastSave.getInstance().clearSession()
                            startActivity(Intent(it, AuthenticationActivity::class.java))
                            it.finishAffinity()
                        }
                    } else {
                        context?.let {
                            Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, "logout error ${response.errorBody?.string()}")
                }
                else -> {
                    // We will do nothing here
                }
            }
        })

        viewModel.getRoomResponse.observe(viewLifecycleOwner, { response ->
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
                        context?.let {
                            Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Log.e(logTag, "getRoomResponse Failure ${response.errorBody?.string()} ")
                    }
                }
                else -> {
                    // We will do nothing here
                }
            }
        })
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
                            getString(R.string.text_cancel)
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
                        DialogUtil.askAlert(mActivity, getString(R.string.dialog_title_logout), getString(R.string.text_yes),getString(R.string.text_no), object : DialogAskListener {
                            override fun onYesClicked() {
                                DialogUtil.hideDialog()
                                val loginType = FastSave.getInstance().getString(Constants.LOGIN_TYPE, "")
                                if (loginType == Constants.LOGIN_TYPE_GOOGLE) {
                                    mGoogleSingInClient?.signOut()
                                } else if (loginType == Constants.LOGIN_TYPE_FACEBOOK) {
                                    LoginManager.getInstance().logOut()
                                }

                                DialogUtil.loadingAlert(mActivity)

                                viewModel.logout(
                                    BodyLogout(
                                        FastSave.getInstance().getString(Constants.MOBILE_UUID, null)
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

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

}