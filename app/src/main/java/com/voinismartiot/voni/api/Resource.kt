package com.voinismartiot.voni.api

import okhttp3.ResponseBody

sealed class Resource<out T> {
    data class Success<out T>(val values: T) : Resource<T>()
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ) : Resource<Nothing>()

    object Loading : Resource<Nothing>()

}