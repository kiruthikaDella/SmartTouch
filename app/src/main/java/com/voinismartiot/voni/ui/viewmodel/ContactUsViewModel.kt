package com.voinismartiot.voni.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyFeedback
import com.voinismartiot.voni.api.model.CommonResponse
import com.voinismartiot.voni.api.repository.ContactUsRepository
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