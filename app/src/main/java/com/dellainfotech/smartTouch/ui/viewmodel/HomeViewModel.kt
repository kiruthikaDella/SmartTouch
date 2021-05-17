package com.dellainfotech.smartTouch.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyAddRoom
import com.dellainfotech.smartTouch.api.body.BodyLogout
import com.dellainfotech.smartTouch.api.body.BodyUpdateUserProfile
import com.dellainfotech.smartTouch.api.model.*
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _logoutResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val logoutResponse: LiveData<Resource<CommonResponse>>
        get() = _logoutResponse

    private val _roomTypeResponse: MutableLiveData<Resource<RoomTypeResponse>> = MutableLiveData()
    val roomTypeResponse: LiveData<Resource<RoomTypeResponse>>
        get() = _roomTypeResponse

    private val _getRoomResponse: MutableLiveData<Resource<GetRoomResponse>> = MutableLiveData()
    val getRoomResponse: LiveData<Resource<GetRoomResponse>>
        get() = _getRoomResponse

    private val _addRoomResponse: MutableLiveData<Resource<AddRoomResponse>> = MutableLiveData()
    val addRoomResponse: LiveData<Resource<AddRoomResponse>>
        get() = _addRoomResponse

    private val _faqResponse: MutableLiveData<Resource<FAQResponse>> = MutableLiveData()
    val faqResponse: LiveData<Resource<FAQResponse>>
        get() = _faqResponse

    private val _getUserProfileResponse: MutableLiveData<Resource<GetProfileResponse>> = MutableLiveData()
    val getUserProfileResponse: LiveData<Resource<GetProfileResponse>>
        get() = _getUserProfileResponse

    private val _updateUserProfileResponse: MutableLiveData<Resource<GetProfileResponse>> = MutableLiveData()
    val updateUserProfileResponse: LiveData<Resource<GetProfileResponse>>
        get() = _updateUserProfileResponse

    fun logout(
        bodyLogout: BodyLogout
    ) = viewModelScope.launch {
        _logoutResponse.value = Resource.Loading
        _logoutResponse.value = homeRepository.logout(bodyLogout)
    }

    fun roomType() = viewModelScope.launch {
        _roomTypeResponse.value = Resource.Loading
        _roomTypeResponse.value = homeRepository.roomType()
    }

    fun getRoom() = viewModelScope.launch {
        _getRoomResponse.value = Resource.Loading
        _getRoomResponse.value = homeRepository.getRoom()
    }

    fun addRoom(bodyAddRoom: BodyAddRoom) = viewModelScope.launch {
        _addRoomResponse.value = Resource.Loading
        _addRoomResponse.value = homeRepository.addRoom(bodyAddRoom)
    }

    fun getFAQ() = viewModelScope.launch {
        _faqResponse.value = Resource.Loading
        _faqResponse.value = homeRepository.getFAQ()
    }

    fun getUserProfile() = viewModelScope.launch {
        _getUserProfileResponse.value = Resource.Loading
        _getUserProfileResponse.value = homeRepository.getUserProfile()
    }

    fun updateUserProfile(bodyUpdateUserProfile: BodyUpdateUserProfile) = viewModelScope.launch {
        _updateUserProfileResponse.value = Resource.Loading
        _updateUserProfileResponse.value = homeRepository.updateUserProfile(bodyUpdateUserProfile)
    }

}