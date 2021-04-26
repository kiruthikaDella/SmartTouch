package com.smartouch.ui.fragments.main.home

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.smartouch.R
import com.smartouch.databinding.FragmentScreenLayoutBinding

/**
 * Created by Jignesh Dangar on 21-04-2021.
 */
class ScreenLayoutModel(context: Context, mBinding: FragmentScreenLayoutBinding) {

    private var mContext = context
    private var binding = mBinding
    private var viewType = VIEW_TYPE.EIGHT_ICONS_VIEW

    enum class VIEW_TYPE {
        EIGHT_ICONS_VIEW,
        SIX_ICONS_VIEW
    }

    fun init() {
        changeViewType(viewType)
        binding.linearEightIconsView.setOnClickListener {
            viewType = VIEW_TYPE.EIGHT_ICONS_VIEW
            changeViewType(viewType)
        }

        binding.linearSixIconsView.setOnClickListener {
            viewType = VIEW_TYPE.SIX_ICONS_VIEW
            changeViewType(viewType)
        }

        setClickListeners()
    }

    private fun changeViewType(viewType: VIEW_TYPE) {
        changeImagesWithViewType(viewType)
        if (viewType == VIEW_TYPE.EIGHT_ICONS_VIEW) {
            mContext?.let {
                binding.ivEightIconsView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_screen_layout_tab_selected))
                binding.viewEightIcons.visibility = View.VISIBLE
                binding.tvEightIconsView.setTextColor(ContextCompat.getColor(it, R.color.daintree))

                binding.ivSixIconsView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_screen_layout_tab))
                binding.viewSixIcons.visibility = View.INVISIBLE
                binding.tvSixIconsView.setTextColor(ContextCompat.getColor(it, R.color.nepal))
            }
        } else if (viewType == VIEW_TYPE.SIX_ICONS_VIEW) {
            mContext?.let {
                binding.ivSixIconsView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_screen_layout_tab_selected))
                binding.viewSixIcons.visibility = View.VISIBLE
                binding.tvSixIconsView.setTextColor(ContextCompat.getColor(it, R.color.daintree))

                binding.ivEightIconsView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_screen_layout_tab))
                binding.viewEightIcons.visibility = View.INVISIBLE
                binding.tvEightIconsView.setTextColor(ContextCompat.getColor(it, R.color.nepal))
            }
        }
    }

    private fun changeImagesWithViewType(viewType: VIEW_TYPE) {
        if (viewType == VIEW_TYPE.EIGHT_ICONS_VIEW) {
            mContext?.let {
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.img_eight_left_most))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.img_eight_left_right))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.img_eight_top_center))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.img_eight_right_most))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.img_eight_middle_center))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.img_eight_bottom_center))
            }
        } else if (viewType == VIEW_TYPE.SIX_ICONS_VIEW) {
            mContext?.let {
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.img_six_left_most))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.img_six_left_right))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.img_six_top_center))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.img_six_right_most))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.img_six_middle_center))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.img_six_bottom_center))
            }
        }
    }

    private fun setClickListeners(){
        binding.ivLeftMost.setOnClickListener {
            if (viewType == VIEW_TYPE.EIGHT_ICONS_VIEW){
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_left_most_selected))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_left_right))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_top_center))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_right_most))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_middle_center))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_bottom_center))
            }else{
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_left_most_selected))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_left_right))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_top_center))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_right_most))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_middle_center))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_bottom_center))
            }
        }

        binding.ivLeftRight.setOnClickListener {
            if (viewType == VIEW_TYPE.EIGHT_ICONS_VIEW){
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_left_most))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_left_right_selected))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_top_center))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_right_most))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_middle_center))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_bottom_center))
            }else{
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_left_most))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_left_right_selected))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_top_center))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_right_most))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_middle_center))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_bottom_center))
            }
        }

        binding.ivTopCenter.setOnClickListener {
            if (viewType == VIEW_TYPE.EIGHT_ICONS_VIEW){
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_left_most))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_left_right))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_top_center_selected))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_right_most))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_middle_center))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_bottom_center))
            }else{
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_left_most))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_left_right))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_top_center_selected))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_right_most))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_middle_center))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_bottom_center))
            }
        }

        binding.ivRightMost.setOnClickListener {
            if (viewType == VIEW_TYPE.EIGHT_ICONS_VIEW){
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_left_most))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_left_right))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_top_center))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_right_most_selected))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_middle_center))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_bottom_center))
            }else{
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_left_most))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_left_right))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_top_center))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_right_most_selected))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_middle_center))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_bottom_center))
            }
        }

        binding.ivMiddleCenter.setOnClickListener {
            if (viewType == VIEW_TYPE.EIGHT_ICONS_VIEW){
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_left_most))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_left_right))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_top_center))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_right_most))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_middle_center_selected))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_bottom_center))
            }else{
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_left_most))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_left_right))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_top_center))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_right_most))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_middle_center_selected))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_bottom_center))
            }
        }

        binding.ivBottomCenter.setOnClickListener {
            if (viewType == VIEW_TYPE.EIGHT_ICONS_VIEW){
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_left_most))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_left_right))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_top_center))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_right_most))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_middle_center))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_eight_bottom_center_selected))
            }else{
                binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_left_most))
                binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_left_right))
                binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_top_center))
                binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_right_most))
                binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_middle_center))
                binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_six_bottom_center_selected))
            }
        }
    }
}