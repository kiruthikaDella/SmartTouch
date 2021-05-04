package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.RoomPanelsAdapter
import com.dellainfotech.smartTouch.common.interfaces.AdapterItemClickListener
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentRoomPanelBinding
import com.dellainfotech.smartTouch.model.RoomPanelModel
import com.dellainfotech.smartTouch.ui.fragments.BaseFragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class RoomPanelFragment : BaseFragment() {

    private val logTag = this::class.java.simpleName
    private lateinit var binding: FragmentRoomPanelBinding
    private val args: RoomPanelFragmentArgs by navArgs()
    private var roomList = arrayListOf<RoomPanelModel>()
    private lateinit var panelAdapter: RoomPanelsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRoomPanelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roomList.clear()
        roomList.add(RoomPanelModel(1, "SMT2028189"))
        roomList.add(RoomPanelModel(2, "SMT2028190"))
        roomList.add(RoomPanelModel(2, "SMT2028191"))
        roomList.add(RoomPanelModel(1, "SMT2028192"))

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvTitle.text = args.roomDetail.title

        panelAdapter = RoomPanelsAdapter(roomList)
        binding.recyclerRoomPanels.adapter = panelAdapter

        panelAdapter.setOnCustomizationClickListener(object :
            AdapterItemClickListener<RoomPanelModel> {
            override fun onItemClick(data: RoomPanelModel) {
                findNavController().navigate(RoomPanelFragmentDirections.actionRoomPanelFragmentToDeviceCustomizationFragment())
            }
        })

        panelAdapter.setOnFeaturesClickListener(object : AdapterItemClickListener<RoomPanelModel> {
            override fun onItemClick(data: RoomPanelModel) {
                findNavController().navigate(RoomPanelFragmentDirections.actionRoomPanelFragmentToDeviceFeaturesFragment())
            }
        })

        panelAdapter.setOnEditClickListener(object : AdapterItemClickListener<RoomPanelModel> {
            override fun onItemClick(data: RoomPanelModel) {
                activity?.let {
                    DialogUtil.editDialog(
                        it,
                        getString(R.string.text_edit),
                        "Switch 1",
                        getString(R.string.text_save),
                        getString(R.string.text_cancel)
                    )
                }
            }

        })

        panelAdapter.setOnSettingsClickListener(object : AdapterItemClickListener<RoomPanelModel> {
            override fun onItemClick(data: RoomPanelModel) {
                findNavController().navigate(RoomPanelFragmentDirections.actionRoomPanelFragmentToDeviceSettingsFragment())
            }

        })

        binding.layoutRoomPanel.ivHidePanel.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
        }

        binding.btnAddPanel.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }
    }


}