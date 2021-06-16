package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.observe
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
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentHomeBinding
import com.dellainfotech.smartTouch.mqtt.NotifyManager
import com.dellainfotech.smartTouch.ui.activities.AuthenticationActivity
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class HomeFragment : ModelBaseFragment<HomeViewModel, FragmentHomeBinding, HomeRepository>(),
    AdapterItemClickListener<GetRoomData> {

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

        navUsername.text = FastSave.getInstance().getString(Constants.USER_FULL_NAME, null)
        navUserEmail.text = FastSave.getInstance().getString(Constants.USER_EMAIL, null)

        roomsAdapter = RoomsAdapter(roomList)
        binding.recyclerRooms.adapter = roomsAdapter

        binding.tvAppVersion.text = "Version - ${BuildConfig.VERSION_NAME}"

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
            Log.e(logTag, " isConnected $isConnected ")
            if (isConnected) {
                Log.e(logTag, " internet is available")
                roomList.toMutableList().clear()
                viewModel.getRoom()
                activity?.let {
                    DialogUtil.loadingAlert(it)
                }
            } else {
                Log.e(logTag, " internet is not available")
            }
        })

        viewModel.logoutResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        activity?.let {
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
                            roomsAdapter.setCallback(this)
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

    override fun onItemClick(data: GetRoomData) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToRoomPanelFragment(
                data
            )
        )
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
                R.id.nav_logout -> {

                    val loginType = FastSave.getInstance().getInt(Constants.LOGIN_TYPE,1)
                    if (loginType == Constants.LOGIN_TYPE_GOOGLE){
                        mGoogleSingInClient?.signOut()
                    }else if (loginType == Constants.LOGIN_TYPE_FACEBOOK){

                    }

                    activity?.let {
                        DialogUtil.loadingAlert(it)
                    }
                    viewModel.logout(
                        BodyLogout(
                            FastSave.getInstance().getString(Constants.MOBILE_UUID, null)
                        )
                    )
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