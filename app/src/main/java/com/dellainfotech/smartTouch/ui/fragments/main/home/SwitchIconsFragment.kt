package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.SwitchIconsAdapter
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.interfaces.DialogAskListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentSwitchIconsBinding
import com.dellainfotech.smartTouch.model.SwitchIconsModel
import com.dellainfotech.smartTouch.ui.fragments.BaseFragment

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */
class SwitchIconsFragment : BaseFragment() {

    private lateinit var binding: FragmentSwitchIconsBinding
    private var switchList = arrayListOf<SwitchIconsModel>()
    private lateinit var adapter: SwitchIconsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSwitchIconsBinding.inflate(inflater, container, false)
        return binding.root
    }

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
        switchList.add(SwitchIconsModel("Switch", "1"))
        switchList.add(SwitchIconsModel("Switch", "2"))
        switchList.add(SwitchIconsModel("Switch", "3"))
        switchList.add(SwitchIconsModel("Switch", "4"))
        switchList.add(SwitchIconsModel("Switch", "5"))
        switchList.add(SwitchIconsModel("Switch", "6"))
        switchList.add(SwitchIconsModel("Switch", "7"))
        switchList.add(SwitchIconsModel("Switch", "8"))
        switchList.add(SwitchIconsModel("Switch", "9"))
        switchList.add(SwitchIconsModel("Switch", "10"))

        adapter = SwitchIconsAdapter(switchList)
        binding.recyclerSwitchIcons.adapter = adapter
        adapter.setOnSwitchClickListener(object : AdapterItemClickListener<SwitchIconsModel> {
            override fun onItemClick(data: SwitchIconsModel) {
                findNavController().navigate(SwitchIconsFragmentDirections.actionSwitchIconsFragmentToSwitchIconsDetailFragment())
            }

        })

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
        binding.btnSynchronize.isEnabled = false
    }

    private fun unlockScreen() {
        binding.btnSynchronize.isEnabled = true
    }

}