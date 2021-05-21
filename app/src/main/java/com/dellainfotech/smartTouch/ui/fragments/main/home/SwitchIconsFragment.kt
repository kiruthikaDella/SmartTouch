package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.SwitchIconsAdapter
import com.dellainfotech.smartTouch.api.model.DeviceSwitchData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentDeviceCustomizationBinding
import com.dellainfotech.smartTouch.databinding.FragmentSwitchIconsBinding
import com.dellainfotech.smartTouch.model.SwitchIconsModel
import com.dellainfotech.smartTouch.ui.fragments.BaseFragment
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */
class SwitchIconsFragment : ModelBaseFragment<HomeViewModel, FragmentSwitchIconsBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: SwitchIconsFragmentArgs by navArgs()
    private var switchList = arrayListOf<DeviceSwitchData>()
    private lateinit var adapter: SwitchIconsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        if (FastSave.getInstance().getBoolean(
                Constants.isSwitchIconsLocked,
                Constants.DEFAULT_SWITCH_ICONS_LOCK_STATUS
            )
        ) {
            lockScreen()
        }

        switchList.clear()

        args.deviceDetail.switchData?.let {

            for (switch in it){
                if (switch.typeOfSwitch == 0){
                    switchList.add(switch)
                }
            }

            adapter = SwitchIconsAdapter(switchList)
            binding.recyclerSwitchIcons.adapter = adapter
            adapter.setOnSwitchClickListener(object : AdapterItemClickListener<DeviceSwitchData> {
                override fun onItemClick(data: DeviceSwitchData) {
                    findNavController().navigate(SwitchIconsFragmentDirections.actionSwitchIconsFragmentToSwitchIconsDetailFragment(data))
                }
            })
        }


        binding.ibLock.setOnClickListener {
            activity?.let {
                DialogUtil.askAlert(
                    it,
                    getString(R.string.dialog_title_text_lock),
                    getString(R.string.text_ok),
                    getString(R.string.text_cancel),
                    object : DialogAskListener {
                        override fun onYesClicked() {
                            FastSave.getInstance().saveBoolean(Constants.isSwitchIconsLocked, true)
                            lockScreen()
                        }

                        override fun onNoClicked() {
                            FastSave.getInstance().saveBoolean(Constants.isSwitchIconsLocked, false)
                            unlockScreen()
                        }

                    }
                )
            }
        }

        binding.btnSynchronize.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun lockScreen() {
        binding.relativeLock.isVisible = true
    }

    private fun unlockScreen() {
        binding.relativeLock.isVisible = false
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSwitchIconsBinding = FragmentSwitchIconsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

}