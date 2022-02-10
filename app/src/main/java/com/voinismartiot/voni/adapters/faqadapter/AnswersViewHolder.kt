package com.voinismartiot.voni.adapters.faqadapter

import android.view.View
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.voinismartiot.voni.R

class AnswersViewHolder(itemView: View) : ChildViewHolder(itemView) {
    private val childTextView: TextView = itemView.findViewById(R.id.tv_answer) as TextView
    fun setAnswer(name: String?) {
        childTextView.text = name
    }

}