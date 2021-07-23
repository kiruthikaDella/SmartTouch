package com.binjal.wifilibrary.listener

interface ConnectResultListener {

    fun onSuccess(message: String)

    fun onFailure(message: String)
}