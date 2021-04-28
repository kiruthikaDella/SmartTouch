package com.smartouch.ui.fragments.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smartouch.R
import com.smartouch.adapters.SwitchIconsAdapter
import com.smartouch.common.interfaces.AdapterItemClickListener
import com.smartouch.common.utils.DialogUtil
import com.smartouch.databinding.FragmentSwitchIconsBinding
import com.smartouch.model.SwitchIconsModel

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */
class SwitchIconsFragment : Fragment() {

    private lateinit var binding: FragmentSwitchIconsBinding
    private var switchList = arrayListOf<SwitchIconsModel>()
    private lateinit var adapter: SwitchIconsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSwitchIconsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        switchList.clear()
        switchList.add(SwitchIconsModel("Switch","1"))
        switchList.add(SwitchIconsModel("Switch","2"))
        switchList.add(SwitchIconsModel("Switch","3"))
        switchList.add(SwitchIconsModel("Switch","4"))
        switchList.add(SwitchIconsModel("Switch","5"))
        switchList.add(SwitchIconsModel("Switch","6"))
        switchList.add(SwitchIconsModel("Switch","7"))
        switchList.add(SwitchIconsModel("Switch","8"))
        switchList.add(SwitchIconsModel("Switch","9"))
        switchList.add(SwitchIconsModel("Switch","10"))

        adapter = SwitchIconsAdapter(switchList)
        binding.recyclerSwitchIcons.adapter = adapter
        adapter.setOnSwitchClickListener(object : AdapterItemClickListener<SwitchIconsModel>{
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
                    getString(R.string.text_cancel)
                )
            }
        }

        binding.btnSynchronize.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}