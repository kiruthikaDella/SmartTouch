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
import com.dellainfotech.smartTouch.api.body.BodySocialLogin
import com.dellainfotech.smartTouch.api.model.CommonResponse
import com.dellainfotech.smartTouch.api.model.LoginResponse
import com.dellainfotech.smartTouch.api.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: MutableLiveData<Resource<LoginResponse>>
        get() = _loginResponse

    private val _signUpResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val signUpResponse: LiveData<Resource<CommonResponse>>
        get() = _signUpResponse

    private val _forgotPasswordResponse: MutableLiveData<Resource<CommonResponse>> =
        MutableLiveData()
    val forgotPasswordResponse: LiveData<Resource<CommonResponse>>
        get() = _forgotPasswordResponse

    private val _socialLoginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val socialLoginResponse: MutableLiveData<Resource<LoginResponse>>
        get() = _socialLoginResponse

    fun login(
        bodyLogin: BodyLogin
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = authRepository.login(bodyLogin)
    }

    fun socialLogin(
        socialLogin: BodySocialLogin
    ) = viewModelScope.launch {
        _socialLoginResponse.value = Resource.Loading
        _socialLoginResponse.value = authRepository.socialLogin(socialLogin)
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