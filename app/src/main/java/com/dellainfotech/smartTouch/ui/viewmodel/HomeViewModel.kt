package com.dellainfotech.smartTouch.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyAddRoom
import com.dellainfotech.smartTouch.api.body.BodyLogout
import com.dellainfotech.smartTouch.api.model.AddRoomResponse
import com.dellainfotech.smartTouch.api.model.CommonResponse
import com.dellainfotech.smartTouch.api.model.GetRoomResponse
import com.dellainfotech.smartTouch.api.model.RoomTypeResponse
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

}