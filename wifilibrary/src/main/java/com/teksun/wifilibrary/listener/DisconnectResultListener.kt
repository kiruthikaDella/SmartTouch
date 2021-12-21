package com.teksun.wifilibrary.listener

interface DisconnectResultListener {

    fun onSuccess(message: String)

    fun onFailure(message: String)
}