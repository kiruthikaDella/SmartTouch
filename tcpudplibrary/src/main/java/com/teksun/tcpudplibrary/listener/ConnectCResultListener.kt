package com.teksun.tcpudplibrary.listener

interface ConnectCResultListener {

    fun onSuccess(message: String)

    fun onConnectFailure(message: String)

    fun onServerDisconnect(message: String)
}