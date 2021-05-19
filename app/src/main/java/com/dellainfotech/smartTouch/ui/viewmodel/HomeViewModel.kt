package com.dellainfotech.smartTouch.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.*
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

    private val _retainStateResponse: MutableLiveData<Resource<AddRoomResponse>> = MutableLiveData()
    val retainStateResponse: LiveData<Resource<AddRoomResponse>>
        get() = _retainStateResponse

    private val _updateRoomResponse: MutableLiveData<Resource<UpdateRoomResponse>> = MutableLiveData()
    val updateRoomResponse: LiveData<Resource<UpdateRoomResponse>>
        get() = _updateRoomResponse

    private val _faqResponse: MutableLiveData<Resource<FAQResponse>> = MutableLiveData()
    val faqResponse: LiveData<Resource<FAQResponse>>
        get() = _faqResponse

    private val _getUserProfileResponse: MutableLiveData<Resource<GetProfileResponse>> =
        MutableLiveData()
    val getUserProfileResponse: LiveData<Resource<GetProfileResponse>>
        get() = _getUserProfileResponse

    private val _updateUserProfileResponse: MutableLiveData<Resource<GetProfileResponse>> = MutableLiveData()
    val updateUserProfileResponse: LiveData<Resource<GetProfileResponse>>
        get() = _updateUserProfileResponse

    private val _changePasswordResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val changePasswordResponse: LiveData<Resource<CommonResponse>>
        get() = _changePasswordResponse

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

    fun updateRoom(bodyUpdateRoom: BodyUpdateRoom) = viewModelScope.launch {
        _updateRoomResponse.value = Resource.Loading
        _updateRoomResponse.value = homeRepository.updateRoom(bodyUpdateRoom)
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

    fun changePassword(bodyChangePassword: BodyChangePassword) = viewModelScope.launch {
        _changePasswordResponse.value = Resource.Loading
        _changePasswordResponse.value = homeRepository.changePassword(bodyChangePassword)
    }

    fun retainState(bodyRetainState: BodyRetainState) = viewModelScope.launch {
        _retainStateResponse.value = Resource.Loading
        _retainStateResponse.value = homeRepository.retainState(bodyRetainState)
    }


    //
    //region Device
    //

    private val _addDeviceResponse: MutableLiveData<Resource<AddDeviceResponse>> = MutableLiveData()
    val addDeviceResponse: LiveData<Resource<AddDeviceResponse>>
        get() = _addDeviceResponse

    private val _getDeviceResponse: MutableLiveData<Resource<GetDeviceResponse>> = MutableLiveData()
    val getDeviceResponse: LiveData<Resource<GetDeviceResponse>>
        get() = _getDeviceResponse

    fun addDevice(bodyAddDevice: BodyAddDevice) = viewModelScope.launch {
        _addDeviceResponse.value = Resource.Loading
        _addDeviceResponse.value = homeRepository.addDevice(bodyAddDevice)
    }

    fun getDevice(roomId: String) = viewModelScope.launch {
        _getDeviceResponse.value = Resource.Loading
        _getDeviceResponse.value = homeRepository.getDeviceData(roomId)
    }


    //
    //endregion
    //
}