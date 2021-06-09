package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.dellainfotech.smartTouch.adapters.SwitchIconsDetailAdapter
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyUpdateSwitchIcon
import com.dellainfotech.smartTouch.api.model.IconListData
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.utils.Constants
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentSwitchIconsDetailBinding
import com.dellainfotech.smartTouch.ui.fragments.ModelBaseFragment
import com.dellainfotech.smartTouch.ui.viewmodel.HomeViewModel

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */
class SwitchIconsDetailFragment :
    ModelBaseFragment<HomeViewModel, FragmentSwitchIconsDetailBinding, HomeRepository>() {

    private val logTag = this::class.java.simpleName
    private val args: SwitchIconsDetailFragmentArgs by navArgs()
    private lateinit var adapter: SwitchIconsDetailAdapter
    private var switchIconList = arrayListOf<IconListData>()
    private var iconData: IconListData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvTitle.text = args.switchDetail.name

        activity?.let {
            DialogUtil.loadingAlert(it)
        }
        viewModel.iconList()

        adapter = SwitchIconsDetailAdapter(switchIconList)
        context?.let {
            binding.recyclerSwitchIcons.layoutManager = GridLayoutManager(it, 4)
        }
        binding.recyclerSwitchIcons.adapter = adapter

        binding.ibSwitch.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSynchronize.setOnClickListener {
            iconData?.let {
                activity?.let { mActivity ->
                    DialogUtil.loadingAlert(mActivity)
                    viewModel.updateSwitchIcon(
                        BodyUpdateSwitchIcon(
                            args.switchDetail.id,
                            it.iconFile
                        )
                    )
                }
            } ?: kotlin.run {
                context?.let {
                    Toast.makeText(it, "Please select icon", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.iconListResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let {
                            switchIconList.addAll(it)
                            adapter.notifyDataSetChanged()
                            iconData = adapter.selectIcon(args.switchDetail)
                            adapter.setOnSwitchClickListener(object :
                                AdapterItemClickListener<IconListData> {
                                override fun onItemClick(data: IconListData) {
                                    iconData = data
                                }

                            })
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(logTag, " iconListResponse Failure ${response.errorBody?.string()} ")
                }
                else -> {
                    //We will do nothing here
                }
            }
        })

        viewModel.updateSwitchIconResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    DialogUtil.hideDialog()
                    context?.let {
                        Toast.makeText(it, response.values.message, Toast.LENGTH_SHORT).show()
                    }
                    if (response.values.status && response.values.code == Constants.API_SUCCESS_CODE) {
                        response.values.data?.let {
                            args.switchDetail.icon = it.icon
                            findNavController().navigateUp()
                        }
                    }
                }
                is Resource.Failure -> {
                    DialogUtil.hideDialog()
                    Log.e(
                        logTag,
                        " updateSwitchIconResponse Failure ${response.errorBody?.string()} "
                    )
                }
                else -> {
                    //We will do nothing here
                }
            }
        })
    }

    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSwitchIconsDetailBinding =
        FragmentSwitchIconsDetailBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): HomeRepository = HomeRepository(networkModel)

}