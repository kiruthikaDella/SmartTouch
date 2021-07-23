package com.teksun.tcpudplibrary.listener

interface ReadWriteValueListenerUDP<T> {

    fun onSuccessUdp(message: String, value: T? = null)

    fun onFailureUdp(message: String)

}