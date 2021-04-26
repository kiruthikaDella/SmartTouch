package com.smartouch.adapters.faqadapter

import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.smartouch.R
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder

class QuestionViewHolder(itemView: View) : GroupViewHolder(itemView) {
    private val tvQuestion: TextView
    private val arrow: ImageView

    fun setQuestion(genre: ExpandableGroup<*>) {
        tvQuestion.text = genre.title
    }

    override fun expand() {
        animateExpand()
    }

    override fun collapse() {
        animateCollapse()
    }

    private fun animateExpand() {
        val rotate = RotateAnimation(
            360f,
            180f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        arrow.animation = rotate
    }

    private fun animateCollapse() {
        val rotate = RotateAnimation(
            180f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        arrow.animation = rotate
    }

    init {
        tvQuestion = itemView.findViewById<View>(R.id.tv_question) as TextView
        arrow =
            itemView.findViewById<View>(R.id.iv_arrow) as ImageView

    }
}