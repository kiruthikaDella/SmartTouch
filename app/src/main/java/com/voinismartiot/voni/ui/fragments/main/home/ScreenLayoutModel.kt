package com.voinismartiot.voni.ui.fragments.main.home

import android.content.Context
import androidx.core.content.ContextCompat
import com.voinismartiot.voni.R
import com.voinismartiot.voni.databinding.FragmentScreenLayoutBinding

class ScreenLayoutModel(context: Context, mBinding: FragmentScreenLayoutBinding) {

    private var mContext = context
    private var binding = mBinding
    private var viewType = ViewType.EIGHT_ICONS_VIEW
    private var screenLayoutType: String? = null
    var screenLayout: String? = null
    val screenLayoutEight = "8"
    val screenLayoutSix = "6"
    val screenLayoutFour = "4"

    companion object {
        const val LEFT_MOST = "left_most"
        const val RIGHT_MOST = "right_most"
        const val LEFT_RIGHT = "left_right"
        const val MIDDLE_CENTER = "middle_center"
        const val TOP_CENTER = "top_center"
        const val BOTTOM_CENTER = "bottom_center"
    }

    var storedViewType: String = screenLayoutEight

    enum class ViewType {
        EIGHT_ICONS_VIEW,
        SIX_ICONS_VIEW,
        FOUR_ICONS_VIEW
    }

    fun init() {
        changeViewType(viewType)
        binding.tvEightIconsView.setOnClickListener {
            screenLayoutType = screenLayoutEight
            viewType = ViewType.EIGHT_ICONS_VIEW
            changeViewType(viewType)
        }

        binding.tvSixIconsView.setOnClickListener {
            screenLayoutType = screenLayoutSix
            viewType = ViewType.SIX_ICONS_VIEW
            changeViewType(viewType)
        }

        binding.tvFourIconsView.setOnClickListener {
            screenLayoutType = screenLayoutFour
            viewType = ViewType.FOUR_ICONS_VIEW
            changeViewType(viewType)
        }

        setClickListeners()
    }

    private fun changeViewType(viewType: ViewType) {
        changeImagesWithViewType(viewType)
        when (viewType) {
            ViewType.EIGHT_ICONS_VIEW -> {
                binding.tvEightIconsView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_screen_layout_tab_selected,
                    0,
                    0,
                    0
                )
                binding.tvEightIconsView.background =
                    ContextCompat.getDrawable(mContext, R.drawable.bottom_line)
                binding.tvEightIconsView.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.daintree
                    )
                )

                binding.tvSixIconsView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_screen_layout_tab,
                    0,
                    0,
                    0
                )
                binding.tvSixIconsView.background =
                    ContextCompat.getDrawable(mContext, R.color.transparent)
                binding.tvSixIconsView.setTextColor(ContextCompat.getColor(mContext, R.color.nepal))

                binding.tvFourIconsView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_screen_layout_tab,
                    0,
                    0,
                    0
                )
                binding.tvFourIconsView.background =
                    ContextCompat.getDrawable(mContext, R.color.transparent)
                binding.tvFourIconsView.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.nepal
                    )
                )
            }
            ViewType.SIX_ICONS_VIEW -> {
                binding.tvSixIconsView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_screen_layout_tab_selected,
                    0,
                    0,
                    0
                )
                binding.tvSixIconsView.background =
                    ContextCompat.getDrawable(mContext, R.drawable.bottom_line)
                binding.tvSixIconsView.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.daintree
                    )
                )

                binding.tvEightIconsView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_screen_layout_tab,
                    0,
                    0,
                    0
                )
                binding.tvEightIconsView.background =
                    ContextCompat.getDrawable(mContext, R.color.transparent)
                binding.tvEightIconsView.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.nepal
                    )
                )

                binding.tvFourIconsView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_screen_layout_tab,
                    0,
                    0,
                    0
                )
                binding.tvFourIconsView.background =
                    ContextCompat.getDrawable(mContext, R.color.transparent)
                binding.tvFourIconsView.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.nepal
                    )
                )
            }
            ViewType.FOUR_ICONS_VIEW -> {
                binding.tvFourIconsView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_screen_layout_tab_selected,
                    0,
                    0,
                    0
                )
                binding.tvFourIconsView.background =
                    ContextCompat.getDrawable(mContext, R.drawable.bottom_line)
                binding.tvFourIconsView.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.daintree
                    )
                )

                binding.tvSixIconsView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_screen_layout_tab,
                    0,
                    0,
                    0
                )
                binding.tvSixIconsView.background =
                    ContextCompat.getDrawable(mContext, R.color.transparent)
                binding.tvSixIconsView.setTextColor(ContextCompat.getColor(mContext, R.color.nepal))

                binding.tvEightIconsView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_screen_layout_tab,
                    0,
                    0,
                    0
                )
                binding.tvEightIconsView.background =
                    ContextCompat.getDrawable(mContext, R.color.transparent)
                binding.tvEightIconsView.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.nepal
                    )
                )
            }
        }
    }

    private fun changeImagesWithViewType(viewType: ViewType) {
        when (viewType) {
            ViewType.EIGHT_ICONS_VIEW -> {
                binding.ivLeftRight.isEnabled = true
                binding.ivLeftRight.alpha = 1f
                binding.ivLeftMost.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_eight_left_most
                    )
                )
                binding.ivLeftRight.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_eight_left_right
                    )
                )
                binding.ivTopCenter.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_eight_top_center
                    )
                )
                binding.ivRightMost.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_eight_right_most
                    )
                )
                binding.ivMiddleCenter.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_eight_middle_center
                    )
                )
                binding.ivBottomCenter.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_eight_bottom_center
                    )
                )
            }
            ViewType.SIX_ICONS_VIEW -> {
                binding.ivLeftRight.isEnabled = true
                binding.ivLeftRight.alpha = 1f
                binding.ivLeftMost.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_six_left_most
                    )
                )
                binding.ivLeftRight.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_six_left_right
                    )
                )
                binding.ivTopCenter.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_six_top_center
                    )
                )
                binding.ivRightMost.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_six_right_most
                    )
                )
                binding.ivMiddleCenter.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_six_middle_center
                    )
                )
                binding.ivBottomCenter.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_six_bottom_center
                    )
                )
            }
            ViewType.FOUR_ICONS_VIEW -> {
                binding.ivLeftRight.isEnabled = false
                binding.ivLeftRight.alpha = 0.5f
                binding.ivLeftMost.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_four_left_most
                    )
                )
                binding.ivLeftRight.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_four_left_right
                    )
                )
                binding.ivTopCenter.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_four_top_center
                    )
                )
                binding.ivRightMost.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_four_right_most
                    )
                )
                binding.ivMiddleCenter.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_four_middle_center
                    )
                )
                binding.ivBottomCenter.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_four_bottom_center
                    )
                )
            }
        }
        selectLayout(viewType)
    }

    private fun selectLayout(viewType: ViewType) {
        when (viewType) {
            ViewType.EIGHT_ICONS_VIEW -> {
                if (storedViewType == screenLayoutEight) {
                    selectDefaultLayout()
                }
            }
            ViewType.SIX_ICONS_VIEW -> {
                if (storedViewType == screenLayoutSix) {
                    selectDefaultLayout()
                }
            }
            ViewType.FOUR_ICONS_VIEW -> {
                if (storedViewType == screenLayoutFour) {
                    selectDefaultLayout()
                }
            }
        }
    }

    private fun selectDefaultLayout() {
        when (screenLayout) {
            LEFT_MOST -> {
                binding.ivLeftMost.performClick()
            }
            RIGHT_MOST -> {
                binding.ivRightMost.performClick()
            }
            LEFT_RIGHT -> {
                binding.ivLeftRight.performClick()
            }
            MIDDLE_CENTER -> {
                binding.ivMiddleCenter.performClick()
            }
            TOP_CENTER -> {
                binding.ivTopCenter.performClick()
            }
            BOTTOM_CENTER -> {
                binding.ivBottomCenter.performClick()
            }
        }
    }

    private fun setClickListeners() {
        binding.ivLeftMost.setOnClickListener {
            screenLayout = LEFT_MOST
            when (viewType) {
                ViewType.EIGHT_ICONS_VIEW -> {
                    storedViewType = screenLayoutEight
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_left_most_selected
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_bottom_center
                        )
                    )
                }
                ViewType.SIX_ICONS_VIEW -> {
                    storedViewType = screenLayoutSix
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_left_most_selected
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_bottom_center
                        )
                    )
                }
                ViewType.FOUR_ICONS_VIEW -> {
                    storedViewType = screenLayoutFour
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_left_most_selected
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_bottom_center
                        )
                    )
                }
            }
        }

        binding.ivLeftRight.setOnClickListener {
            screenLayout = LEFT_RIGHT
            when (viewType) {
                ViewType.EIGHT_ICONS_VIEW -> {
                    storedViewType = screenLayoutEight
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_left_right_selected
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_bottom_center
                        )
                    )
                }
                ViewType.SIX_ICONS_VIEW -> {
                    storedViewType = screenLayoutSix
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_left_right_selected
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_bottom_center
                        )
                    )
                }
                ViewType.FOUR_ICONS_VIEW -> {
                    storedViewType = screenLayoutFour
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_left_right_selected
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_bottom_center
                        )
                    )
                }
            }
        }

        binding.ivTopCenter.setOnClickListener {
            screenLayout = TOP_CENTER
            when (viewType) {
                ViewType.EIGHT_ICONS_VIEW -> {
                    storedViewType = screenLayoutEight
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_top_center_selected
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_bottom_center
                        )
                    )
                }
                ViewType.SIX_ICONS_VIEW -> {
                    storedViewType = screenLayoutSix
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_top_center_selected
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_bottom_center
                        )
                    )
                }
                ViewType.FOUR_ICONS_VIEW -> {
                    storedViewType = screenLayoutFour
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_top_center_selected
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_bottom_center
                        )
                    )
                }
            }
        }

        binding.ivRightMost.setOnClickListener {
            screenLayout = RIGHT_MOST
            when (viewType) {
                ViewType.EIGHT_ICONS_VIEW -> {
                    storedViewType = screenLayoutEight
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_right_most_selected
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_bottom_center
                        )
                    )
                }
                ViewType.SIX_ICONS_VIEW -> {
                    storedViewType = screenLayoutSix
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_right_most_selected
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_bottom_center
                        )
                    )
                }
                ViewType.FOUR_ICONS_VIEW -> {
                    storedViewType = screenLayoutFour
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_right_most_selected
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_bottom_center
                        )
                    )
                }
            }
        }

        binding.ivMiddleCenter.setOnClickListener {
            screenLayout = MIDDLE_CENTER
            when (viewType) {
                ViewType.EIGHT_ICONS_VIEW -> {
                    storedViewType = screenLayoutEight
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_middle_center_selected
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_bottom_center
                        )
                    )
                }
                ViewType.SIX_ICONS_VIEW -> {
                    storedViewType = screenLayoutSix
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_middle_center_selected
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_bottom_center
                        )
                    )
                }
                ViewType.FOUR_ICONS_VIEW -> {
                    storedViewType = screenLayoutFour
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_middle_center_selected
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_bottom_center
                        )
                    )
                }
            }
        }

        binding.ivBottomCenter.setOnClickListener {
            screenLayout = BOTTOM_CENTER
            when (viewType) {
                ViewType.EIGHT_ICONS_VIEW -> {
                    storedViewType = screenLayoutEight
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_eight_bottom_center_selected
                        )
                    )
                }
                ViewType.SIX_ICONS_VIEW -> {
                    storedViewType = screenLayoutSix
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_six_bottom_center_selected
                        )
                    )
                }
                ViewType.FOUR_ICONS_VIEW -> {
                    storedViewType = screenLayoutFour
                    binding.ivLeftMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_left_most
                        )
                    )
                    binding.ivLeftRight.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_left_right
                        )
                    )
                    binding.ivTopCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_top_center
                        )
                    )
                    binding.ivRightMost.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_right_most
                        )
                    )
                    binding.ivMiddleCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_middle_center
                        )
                    )
                    binding.ivBottomCenter.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_four_bottom_center_selected
                        )
                    )
                }
            }
        }
    }
}