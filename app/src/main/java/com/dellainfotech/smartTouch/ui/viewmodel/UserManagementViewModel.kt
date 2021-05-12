package com.dellainfotech.smartTouch.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyFeedback
import com.dellainfotech.smartTouch.api.body.BodySubordinateUser
import com.dellainfotech.smartTouch.api.model.CommonResponse
import com.dellainfotech.smartTouch.api.repository.UserManagementRepository
import kotlinx.coroutines.launch

class UserManagementViewModel @ViewModelInject constructor(
    private val userManagementRepository: UserManagementRepository
) : ViewModel() {

    private val _addSubordinateUserResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val addSubordinateUserResponse: LiveData<Resource<CommonResponse>>
        get() = _addSubordinateUserResponse

    fun addSubordinateUser(bodySubordinateUser: BodySubordinateUser) = viewModelScope.launch {
        _addSubordinateUserResponse.value = Resource.Loading
        _addSubordinateUserResponse.value = userManagementRepository.addSubordinateUser(bodySubordinateUser)
    }

}