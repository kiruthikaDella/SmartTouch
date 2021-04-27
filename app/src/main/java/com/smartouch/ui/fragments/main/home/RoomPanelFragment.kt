package com.smartouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.smartouch.R
import com.smartouch.adapters.RoomPanelsAdapter
import com.smartouch.common.interfaces.AdapterItemClickListener
import com.smartouch.common.utils.dialog
import com.smartouch.databinding.FragmentRoomPanelBinding
import com.smartouch.model.RoomPanelModel
import com.smartouch.ui.fragments.BaseFragment
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
                    dialog.editDialog(
                        it,
                        getString(R.string.text_edit),
                        "Switch 1",
                        getString(R.string.text_save),
                        getString(R.string.text_cancel),
                        null
                    )
                }
            }

        })

        panelAdapter.setOnSettingsClickListener(object : AdapterItemClickListener<RoomPanelModel>{
            override fun onItemClick(data: RoomPanelModel) {
                findNavController().navigate(RoomPanelFragmentDirections.actionRoomPanelFragmentToDeviceSettingsFragment())
            }

        })

        binding.ivHidePanel.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
        }

        binding.btnAddPanel.setOnClickListener {
            binding.layoutSlidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }
    }


}