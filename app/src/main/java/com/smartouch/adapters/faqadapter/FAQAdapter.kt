package com.smartouch.adapters.faqadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smartouch.R
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class FAQAdapter(groups: List<ExpandableGroup<*>?>?) :
    ExpandableRecyclerViewAdapter<QuestionViewHolder, AnswersViewHolder>(groups) {
    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onCreateChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnswersViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_answers, parent, false)
        return AnswersViewHolder(view)
    }

    override fun onBindChildViewHolder(
        holder: AnswersViewHolder, flatPosition: Int,
        group: ExpandableGroup<*>, childIndex: Int
    ) {
        val artist: AnswerModel? = (group as QuestionModel).getItems().get(childIndex)
        holder.setArtistName(artist?.name)
    }

    override fun onBindGroupViewHolder(
        holder: QuestionViewHolder, flatPosition: Int,
        group: ExpandableGroup<*>
    ) {
        holder.setQuestion(group)
    }
}