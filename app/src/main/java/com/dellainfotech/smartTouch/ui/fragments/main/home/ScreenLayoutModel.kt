package com.dellainfotech.smartTouch.ui.fragments.main.home

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.databinding.FragmentScreenLayoutBinding

/**
 * Created by Jignesh Dangar on 21-04-2021.
 */
class ScreenLayoutModel(context: Context, mBinding: FragmentScreenLayoutBinding) {

    private val logTag = this::class.java.simpleName

    private var mContext = context
    private var binding = mBinding
    private var viewType = VIEW_TYPE.EIGHT_ICONS_VIEW
    val screenLayoutEight = "8"
    val screenLayoutSix = "6"
    val screenLayoutFour = "4"
    val LEFT_MOST = "left_most"
    val RIGHT_MOST = "right_most"
    val LEFT_RIGHT = "left_right"
    val MIDDLE_CENTER = "middle_center"
    val TOP_CENTER = "top_center"
    val BOTTOM_CENTER = "bottom_center"

    enum class VIEW_TYPE {
        EIGHT_ICONS_VIEW,
        SIX_ICONS_VIEW,
        FOUR_ICONS_VIEW
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

        binding.linearFourIconsView.setOnClickListener {
            viewType = VIEW_TYPE.FOUR_ICONS_VIEW
            changeViewType(viewType)
        }

        setClickListeners()
    }

    fun changeViewType(viewType: VIEW_TYPE) {
        changeImagesWithViewType(viewType)
        when (viewType) {
            VIEW_TYPE.EIGHT_ICONS_VIEW -> {
                mContext?.let {
                    binding.ivEightIconsView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_screen_layout_tab_selected))
                    binding.viewEightIcons.visibility = View.VISIBLE
                    binding.tvEightIconsView.setTextColor(ContextCompat.getColor(it, R.color.daintree))

                    binding.ivSixIconsView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_screen_layout_tab))
                    binding.viewSixIcons.visibility = View.INVISIBLE
                    binding.tvSixIconsView.setTextColor(ContextCompat.getColor(it, R.color.nepal))

                    binding.ivFourIconsView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_screen_layout_tab))
                    binding.viewFourIcons.visibility = View.INVISIBLE
                    binding.tvFourIconsView.setTextColor(ContextCompat.getColor(it, R.color.nepal))
                }
            }
            VIEW_TYPE.SIX_ICONS_VIEW -> {
                mContext?.let {
                    binding.ivSixIconsView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_screen_layout_tab_selected))
                    binding.viewSixIcons.visibility = View.VISIBLE
                    binding.tvSixIconsView.setTextColor(ContextCompat.getColor(it, R.color.daintree))

                    binding.ivEightIconsView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_screen_layout_tab))
                    binding.viewEightIcons.visibility = View.INVISIBLE
                    binding.tvEightIconsView.setTextColor(ContextCompat.getColor(it, R.color.nepal))

                    binding.ivFourIconsView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_screen_layout_tab))
                    binding.viewFourIcons.visibility = View.INVISIBLE
                    binding.tvFourIconsView.setTextColor(ContextCompat.getColor(it, R.color.nepal))
                }
            }
            VIEW_TYPE.FOUR_ICONS_VIEW -> {
                mContext?.let {

                    binding.ivFourIconsView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_screen_layout_tab_selected))
                    binding.viewFourIcons.visibility = View.VISIBLE
                    binding.tvFourIconsView.setTextColor(ContextCompat.getColor(it, R.color.daintree))

                    binding.ivEightIconsView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_screen_layout_tab))
                    binding.viewEightIcons.visibility = View.INVISIBLE
                    binding.tvEightIconsView.setTextColor(ContextCompat.getColor(it, R.color.nepal))

                    binding.ivSixIconsView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_screen_layout_tab))
                    binding.viewSixIcons.visibility = View.INVISIBLE
                    binding.tvSixIconsView.setTextColor(ContextCompat.getColor(it, R.color.nepal))
                }
            }
        }
    }

    private fun changeImagesWithViewType(viewType: VIEW_TYPE) {
        when (viewType) {
            VIEW_TYPE.EIGHT_ICONS_VIEW -> {
                mContext?.let {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_eight_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_eight_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_eight_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_eight_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_eight_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_eight_bottom_center))
                }
            }
            VIEW_TYPE.SIX_ICONS_VIEW -> {
                mContext?.let {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_six_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_six_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_six_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_six_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_six_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_six_bottom_center))
                }
            }
            VIEW_TYPE.FOUR_ICONS_VIEW -> {
                mContext?.let {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_four_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_four_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_four_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_four_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_four_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_four_bottom_center))
                }
            }
        }
    }

   private fun setClickListeners(){
        binding.ivLeftMost.setOnClickListener {
            when (viewType) {
                VIEW_TYPE.EIGHT_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_left_most_selected))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_bottom_center))
                }
                VIEW_TYPE.SIX_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_left_most_selected))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_bottom_center))
                }
                VIEW_TYPE.FOUR_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_left_most_selected))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_bottom_center))
                }
            }
        }

        binding.ivLeftRight.setOnClickListener {
            when (viewType) {
                VIEW_TYPE.EIGHT_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_left_right_selected))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_bottom_center))
                }
                VIEW_TYPE.SIX_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_left_right_selected))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_bottom_center))
                }
                VIEW_TYPE.FOUR_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_left_right_selected))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_bottom_center))
                }
            }
        }

        binding.ivTopCenter.setOnClickListener {
            when (viewType) {
                VIEW_TYPE.EIGHT_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_top_center_selected))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_bottom_center))
                }
                VIEW_TYPE.SIX_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_top_center_selected))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_bottom_center))
                }
                VIEW_TYPE.FOUR_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_top_center_selected))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_bottom_center))
                }
            }
        }

        binding.ivRightMost.setOnClickListener {
            when (viewType) {
                VIEW_TYPE.EIGHT_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_right_most_selected))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_bottom_center))
                }
                VIEW_TYPE.SIX_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_right_most_selected))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_bottom_center))
                }
                VIEW_TYPE.FOUR_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_right_most_selected))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_bottom_center))
                }
            }
        }

        binding.ivMiddleCenter.setOnClickListener {
            when (viewType) {
                VIEW_TYPE.EIGHT_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_middle_center_selected))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_bottom_center))
                }
                VIEW_TYPE.SIX_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_middle_center_selected))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_bottom_center))
                }
                VIEW_TYPE.FOUR_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_middle_center_selected))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_bottom_center))
                }
            }
        }

        binding.ivBottomCenter.setOnClickListener {
            when (viewType) {
                VIEW_TYPE.EIGHT_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_eight_bottom_center_selected))
                }
                VIEW_TYPE.SIX_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_six_bottom_center_selected))
                }
                VIEW_TYPE.FOUR_ICONS_VIEW -> {
                    binding.ivLeftMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_left_most))
                    binding.ivLeftRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_left_right))
                    binding.ivTopCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_top_center))
                    binding.ivRightMost.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_right_most))
                    binding.ivMiddleCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_middle_center))
                    binding.ivBottomCenter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_four_bottom_center_selected))
                }
            }
        }
    }
}