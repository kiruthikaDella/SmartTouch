package com.smartouch.adapters.faqadapter

import android.view.View
import android.widget.TextView
import com.smartouch.R
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder

class AnswersViewHolder(itemView: View) : ChildViewHolder(itemView) {
    private val childTextView: TextView
    fun setArtistName(name: String?) {
        childTextView.text = name
    }

    init {
        childTextView =
            itemView.findViewById(R.id.tv_answer) as TextView
    }
}