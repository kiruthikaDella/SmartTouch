package com.voinismartiot.voni.adapters.faqadapter

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class QuestionModel(
    title: String?,
    items: List<AnswerModel?>?
) :
    ExpandableGroup<AnswerModel?>(title, items) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}