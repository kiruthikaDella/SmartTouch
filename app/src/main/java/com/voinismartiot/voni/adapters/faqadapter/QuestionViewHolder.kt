package com.voinismartiot.voni.adapters.faqadapter

import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import com.voinismartiot.voni.R

class QuestionViewHolder(itemView: View) : GroupViewHolder(itemView) {
    private val tvQuestion: TextView = itemView.findViewById<View>(R.id.tv_question) as TextView
    private val arrow: ImageView = itemView.findViewById<View>(R.id.iv_arrow) as ImageView

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

}