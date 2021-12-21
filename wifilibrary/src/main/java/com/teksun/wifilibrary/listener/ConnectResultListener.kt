package com.teksun.wifilibrary.listener

interface ConnectResultListener {

    fun onSuccess(message: String)

    fun onFailure(message: String)
}