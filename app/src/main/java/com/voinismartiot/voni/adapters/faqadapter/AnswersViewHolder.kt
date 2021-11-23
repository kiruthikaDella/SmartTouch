package com.voinismartiot.voni.adapters.faqadapter

import android.view.View
import android.widget.TextView
import com.voinismartiot.voni.R
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder

class AnswersViewHolder(itemView: View) : ChildViewHolder(itemView) {
    private val childTextView: TextView = itemView.findViewById(R.id.tv_answer) as TextView
    fun setArtistName(name: String?) {
        childTextView.text = name
    }

}