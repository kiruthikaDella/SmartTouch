package com.dellainfotech.smartTouch.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.api.body.BodyForgotPassword
import com.dellainfotech.smartTouch.api.body.BodyLogin
import com.dellainfotech.smartTouch.api.body.BodySignUp
import com.dellainfotech.smartTouch.api.model.ForgotPasswordResponse
import com.dellainfotech.smartTouch.api.model.LoginResponse
import com.dellainfotech.smartTouch.api.model.SignUpResponse
import com.dellainfotech.smartTouch.api.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse

    private val _signUpResponse: MutableLiveData<Resource<SignUpResponse>> = MutableLiveData()
    val signUpResponse: LiveData<Resource<SignUpResponse>>
        get() = _signUpResponse

    private val _forgotPasswordResponse: MutableLiveData<Resource<ForgotPasswordResponse>> = MutableLiveData()
    val forgotPasswordResponse: LiveData<Resource<ForgotPasswordResponse>>
        get() = _forgotPasswordResponse

    fun login(
        bodyLogin: BodyLogin
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = authRepository.login(bodyLogin)
    }

    fun signUp(
        bodySignUp: BodySignUp
    ) = viewModelScope.launch {
        _signUpResponse.value = Resource.Loading
        _signUpResponse.value = authRepository.signUp(bodySignUp)
    }

    fun forgotPassword(
       bodyForgotPassword: BodyForgotPassword
    ) = viewModelScope.launch {
        _forgotPasswordResponse.value = Resource.Loading
        _forgotPasswordResponse.value = authRepository.forgotPassword(bodyForgotPassword)
    }

}