package com.teksun.tcpudplibrary.listener

interface CloseSocketListener {

    fun onSuccess(message: String)

    fun onFailure(message: String)
}