package com.teksun.tcpudplibrary.listener

interface ConnectCResultListener {

    fun onSuccess(message: String)

    fun onFailure(message: String)

    fun onServerDisconnect(message: String)
}