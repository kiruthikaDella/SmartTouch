package com.voinismartiot.voni.adapters.faqadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.voinismartiot.voni.R

class FAQAdapter(faqList: List<ExpandableGroup<*>?>?) :
    ExpandableRecyclerViewAdapter<QuestionViewHolder, AnswersViewHolder>(
        faqList
    ) {
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
        val answerModel: AnswerModel? = (group as QuestionModel).items[childIndex]
        holder.setAnswer(answerModel?.name)
    }

    override fun onBindGroupViewHolder(
        holder: QuestionViewHolder, flatPosition: Int,
        group: ExpandableGroup<*>
    ) {
        holder.setQuestion(group)
    }

}