package com.teksun.wifilibrary.listener

interface RemoveNetworkResultListener {

    fun onSuccess(message: String)

    fun onFailure(message: String)
}