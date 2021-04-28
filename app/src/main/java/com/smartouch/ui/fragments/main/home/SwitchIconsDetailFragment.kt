package com.smartouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.smartouch.R
import com.smartouch.adapters.SwitchIconsDetailAdapter
import com.smartouch.databinding.FragmentSwitchIconsDetailBinding
import com.smartouch.model.SwitchIconsDetailModel

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */
class SwitchIconsDetailFragment : Fragment() {

    private lateinit var binding: FragmentSwitchIconsDetailBinding
    private lateinit var adapter: SwitchIconsDetailAdapter
    private var switchIconList = arrayListOf<SwitchIconsDetailModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSwitchIconsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_fan, "Fan"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_bulb, "Bulb"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_usb_type_a, "USB Type A"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_usb_type_c, "USB Type C"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_chandelier, "Chandelier"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_table_lamp, "Table Lamp"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_floor_lamp, "Floor Lamp"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_night_lamp, "Night Lamp"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_ac, "AC"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_ceiling_light, "Ceiling Light"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_exhaust_fan, "Exhaust Fan"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_wall_sconces, "Wall Sconces"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_valance_light, "Valance Light"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_spotlight, "Spotlight"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_cove_light, "Cove Light"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_pendant, "Pendant"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_geyser, "Geyser"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_track_light, "Track Light"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_vanity_light, "Vanity Light"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_under_cabinet, "Under Cabinet"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_wall_grazer, "Wall Grazer"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_wall_washer, "Wall Washer"))
        switchIconList.add(SwitchIconsDetailModel(R.drawable.ic_foot_light, "Foot Light"))

        adapter = SwitchIconsDetailAdapter(switchIconList)
        context?.let {
            binding.recyclerSwitchIcons.layoutManager = GridLayoutManager(it, 4)
        }
        binding.recyclerSwitchIcons.adapter = adapter

        binding.ibSwitch.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}