package com.dellainfotech.smartTouch.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyLogout
import com.dellainfotech.smartTouch.api.model.CommonResponse
import com.dellainfotech.smartTouch.api.repository.HomeRepository
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _logoutResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val logoutResponse: LiveData<Resource<CommonResponse>>
        get() = _logoutResponse

    fun logout(
        bodyLogout: BodyLogout
    ) = viewModelScope.launch {
        _logoutResponse.value = Resource.Loading
        _logoutResponse.value = homeRepository.logout(bodyLogout)
    }

}