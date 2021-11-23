package com.voinismartiot.voni.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voinismartiot.voni.api.Resource
import com.voinismartiot.voni.api.body.BodyAddSubordinateUser
import com.voinismartiot.voni.api.model.CommonResponse
import com.voinismartiot.voni.api.model.SubordinateUserResponse
import com.voinismartiot.voni.api.repository.UserManagementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val userManagementRepository: UserManagementRepository
) : ViewModel() {

    private val _addSubordinateUserResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val addSubordinateUserResponse = _addSubordinateUserResponse.asSharedFlow()

    private val _getSubordinateUserResponse = MutableSharedFlow<Resource<SubordinateUserResponse>>()
    val getSubordinateUserResponse = _getSubordinateUserResponse.asSharedFlow()

    private val _deleteSubordinateUserResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val deleteSubordinateUserResponse = _deleteSubordinateUserResponse.asSharedFlow()

    fun addSubordinateUser(bodyAddSubordinateUser: BodyAddSubordinateUser) = viewModelScope.launch {
        _addSubordinateUserResponse.emit(Resource.Loading)
        _addSubordinateUserResponse.emit(
            userManagementRepository.addSubordinateUser(
                bodyAddSubordinateUser
            )
        )
    }

    fun getSubordinateUser() = viewModelScope.launch {
        _getSubordinateUserResponse.emit(Resource.Loading)
        _getSubordinateUserResponse.emit(userManagementRepository.getSubordinateUser())
    }

    fun deleteSubordinateUser(subordinateUserId: String) =
        viewModelScope.launch {
            _deleteSubordinateUserResponse.emit(Resource.Loading)
            _deleteSubordinateUserResponse.emit(
                userManagementRepository.deleteSubordinateUser(
                    subordinateUserId
                )
            )
        }

}