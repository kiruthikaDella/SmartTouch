package com.dellainfotech.smartTouch.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyAddSubordinateUser
import com.dellainfotech.smartTouch.api.model.CommonResponse
import com.dellainfotech.smartTouch.api.model.SubordinateUserResponse
import com.dellainfotech.smartTouch.api.repository.UserManagementRepository
import kotlinx.coroutines.launch

class UserManagementViewModel @ViewModelInject constructor(
    private val userManagementRepository: UserManagementRepository
) : ViewModel() {

    private val _addSubordinateUserResponse: MutableLiveData<Resource<CommonResponse>> =
        MutableLiveData()
    val addSubordinateUserResponse: LiveData<Resource<CommonResponse>>
        get() = _addSubordinateUserResponse

    private val _getSubordinateUserResponse: MutableLiveData<Resource<SubordinateUserResponse>> =
        MutableLiveData()
    val getSubordinateUserResponse: LiveData<Resource<SubordinateUserResponse>>
        get() = _getSubordinateUserResponse

    private val _deleteSubordinateUserResponse: MutableLiveData<Resource<CommonResponse>> =
        MutableLiveData()
    val deleteSubordinateUserResponse: LiveData<Resource<CommonResponse>>
        get() = _deleteSubordinateUserResponse

    fun addSubordinateUser(bodyAddSubordinateUser: BodyAddSubordinateUser) = viewModelScope.launch {
        _addSubordinateUserResponse.value = Resource.Loading
        _addSubordinateUserResponse.value =
            userManagementRepository.addSubordinateUser(bodyAddSubordinateUser)
    }

    fun getSubordinateUser() = viewModelScope.launch {
        _getSubordinateUserResponse.value = Resource.Loading
        _getSubordinateUserResponse.value = userManagementRepository.getSubordinateUser()
    }

    fun deleteSubordinateUser(subordinateUserId: String) =
        viewModelScope.launch {
            _deleteSubordinateUserResponse.value = Resource.Loading
            _deleteSubordinateUserResponse.value =
                userManagementRepository.deleteSubordinateUser(subordinateUserId)
        }

}