package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.adapters.DeviceSceneAdapter
import com.dellainfotech.smartTouch.common.utils.DialogUtil
import com.dellainfotech.smartTouch.databinding.FragmentCreateSceneBinding
import com.dellainfotech.smartTouch.ui.fragments.BaseFragment
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Jignesh Dangar on 23-04-2021.
 */

class CreateSceneFragment : BaseFragment() {

    private lateinit var binding: FragmentCreateSceneBinding
    private lateinit var deviceSceneAdapter: DeviceSceneAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateSceneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        context?.let {
            deviceSceneAdapter = DeviceSceneAdapter(it)
            binding.recyclerScenes.adapter = deviceSceneAdapter
        }

        binding.tvDaily.setOnClickListener {

            context?.let { ctx ->
                val popup = PopupMenu(ctx, binding.tvDaily)
                popup.menuInflater.inflate(R.menu.scene_frequency_menu, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    binding.tvDaily.text = item.title
                    true
                }
                popup.show()
            }

        }

        binding.ivEditCreateScene.setOnClickListener {
            activity?.let {
                DialogUtil.editDialog(
                    it, getString(R.string.text_scene_name), "Living Room", getString(
                        R.string.text_save
                    ), getString(
                        R.string.text_cancel
                    )
                )
            }
        }

        setTime()

        binding.tvTime.setOnClickListener {
            context?.let { mContext ->
                val mTimePicker: TimePickerDialog
                val mCurrentTime = Calendar.getInstance()
                val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
                val minute = mCurrentTime.get(Calendar.MINUTE)

                mTimePicker = TimePickerDialog(
                    mContext,
                    { _, hourOfDay, min ->
                        val cal = Calendar.getInstance()
                        cal[Calendar.HOUR_OF_DAY] = hourOfDay
                        cal[Calendar.MINUTE] = min
                        val formatter: Format
                        formatter = SimpleDateFormat("hh:mm a", Locale.US)
                        val time = "<font color='#1A8EFF'>${
                            formatter.format(cal.time).dropLast(3)
                        }</font><font color='#011B25'> ${
                            formatter.format(
                                cal.time
                            ).takeLast(2).toLowerCase(Locale.getDefault())
                        }</font>"
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            binding.tvTime.text = Html.fromHtml(time, Html.FROM_HTML_MODE_LEGACY)
                        } else {
                            binding.tvTime.text = Html.fromHtml(time)
                        }
                    },
                    hour,
                    minute,
                    false
                )

                mTimePicker.show()
            }

        }
    }

    private fun setTime() {
        val formatter = SimpleDateFormat("hh:mm a", Locale.US)
        val time = "<font color='#1A8EFF'>${
            formatter.format(Calendar.getInstance().time).dropLast(3)
        }</font><font color='#011B25'> ${
            formatter.format(
                Calendar.getInstance().time
            ).takeLast(2).toLowerCase(Locale.getDefault())
        }</font>"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.tvTime.text = Html.fromHtml(time, Html.FROM_HTML_MODE_LEGACY)
        } else {
            binding.tvTime.text = Html.fromHtml(time)
        }
    }

}