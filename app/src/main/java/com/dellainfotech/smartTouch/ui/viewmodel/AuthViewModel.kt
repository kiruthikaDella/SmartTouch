package com.dellainfotech.smartTouch.ui.viewmodel

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginResponse = MutableSharedFlow<Resource<LoginResponse>>()
    val loginResponse = _loginResponse.asSharedFlow()

    private val _signUpResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val signUpResponse = _signUpResponse.asSharedFlow()

    private val _forgotPasswordResponse = MutableSharedFlow<Resource<CommonResponse>>()
    val forgotPasswordResponse = _forgotPasswordResponse.asSharedFlow()

    private val _socialLoginResponse = MutableSharedFlow<Resource<LoginResponse>>()
    val socialLoginResponse = _socialLoginResponse.asSharedFlow()

    fun login(
        bodyLogin: BodyLogin
    ) = viewModelScope.launch {
        _loginResponse.emit(Resource.Loading)
        _loginResponse.emit(authRepository.login(bodyLogin))
    }

    fun socialLogin(
        socialLogin: BodySocialLogin
    ) = viewModelScope.launch {
        _socialLoginResponse.emit(Resource.Loading)
        _socialLoginResponse.emit(authRepository.socialLogin(socialLogin))
    }

    fun signUp(
        bodySignUp: BodySignUp
    ) = viewModelScope.launch {
        _signUpResponse.emit(Resource.Loading)
        _signUpResponse.emit(authRepository.signUp(bodySignUp))
    }

    fun forgotPassword(
        bodyForgotPassword: BodyForgotPassword
    ) = viewModelScope.launch {
        _forgotPasswordResponse.emit(Resource.Loading)
        _forgotPasswordResponse.emit(authRepository.forgotPassword(bodyForgotPassword))
    }

}