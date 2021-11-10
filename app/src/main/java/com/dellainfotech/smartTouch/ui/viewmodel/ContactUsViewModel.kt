package com.dellainfotech.smartTouch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyFeedback
import com.dellainfotech.smartTouch.api.model.CommonResponse
import com.dellainfotech.smartTouch.api.repository.ContactUsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactUsViewModel @Inject constructor(
    private val contactUsRepository: ContactUsRepository
) : ViewModel() {

    private val _addFeedbackResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val addFeedbackResponse = _addFeedbackResponse.asSharedFlow()

    fun addFeedback(bodyFeedback: BodyFeedback) = viewModelScope.launch {
        _addFeedbackResponse.emit(Resource.Loading)
        _addFeedbackResponse.emit(contactUsRepository.addFeedback(bodyFeedback))
    }

}