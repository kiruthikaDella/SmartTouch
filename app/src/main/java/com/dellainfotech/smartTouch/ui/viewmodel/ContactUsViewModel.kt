package com.dellainfotech.smartTouch.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyFeedback
import com.dellainfotech.smartTouch.api.model.CommonResponse
import com.dellainfotech.smartTouch.api.repository.ContactUsRepository
import kotlinx.coroutines.launch

class ContactUsViewModel @ViewModelInject constructor(
    private val contactUsRepository: ContactUsRepository
) : ViewModel() {

    private val _addFeedbackResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val addFeedbackResponse: MutableLiveData<Resource<CommonResponse>>
        get() = _addFeedbackResponse

    fun addFeedback(bodyFeedback: BodyFeedback) = viewModelScope.launch {
        _addFeedbackResponse.value = Resource.Loading
        _addFeedbackResponse.value = contactUsRepository.addFeedback(bodyFeedback)
    }

}