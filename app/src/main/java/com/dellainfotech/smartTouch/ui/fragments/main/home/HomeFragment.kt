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
import androidx.navigation.fragment.findNavController
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.HomeRoomsAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyLogout
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentHomeBinding
import com.dellainfotech.smartTouch.model.HomeRoomModel
import com.dellainfotech.smartTouch.ui.activities.AuthenticationActivity
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel


/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class HomeFragment : ModelBaseFragment<HomeViewModel, FragmentHomeBinding, HomeRepository>(),
    AdapterItemClickListener<HomeRoomModel> {

    private val logTag = this::class.java.simpleName
    private lateinit var roomsAdapter: HomeRoomsAdapter
    private var roomList = arrayListOf<HomeRoomModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivSideNavigation.setOnClickListener {
            openOrCloseDrawer()
        }

        val headerView: View = binding.sideNavigationView.getHeaderView(0)
        val navUsername = headerView.findViewById(R.id.tv_user_name) as TextView
        val navUserEmail = headerView.findViewById(R.id.tv_user_email) as TextView

        navUsername.text = FastSave.getInstance().getString(Constants.USER_FULL_NAME,null)
        navUserEmail.text = FastSave.getInstance().getString(Constants.USER_EMAIL,null)

        // initializing navigation menu
        setUpNavigationView()

        roomList.clear()
        roomList.add(
            HomeRoomModel(
                R.drawable.img_living_room,
                getString(R.string.text_living_room)
            )
        )
        roomList.add(HomeRoomModel(R.drawable.img_bedroom, getString(R.string.text_bedroom)))
        roomList.add(HomeRoomModel(R.drawable.img_kitchen, getString(R.string.text_kitchen)))
        roomList.add(
            HomeRoomModel(
                R.drawable.img_master_bedroom,
                getString(R.string.text_master_bedroom)
            )
        )
        roomsAdapter = HomeRoomsAdapter(roomList)
        binding.recyclerRooms.adapter = roomsAdapter
        roomsAdapter.setCallback(this)

        apiCall()
    }

    private fun apiCall() {
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
                    Log.e(logTag, "logout error ${response.errorBody}")
                }
            }
        })
    }

    override fun onItemClick(data: HomeRoomModel) {
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
                    activity?.let {
                        DialogUtil.loadingAlert(it, isCancelable = false)
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