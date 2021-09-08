package com.teksun.tcpudplibrary.listener

interface ReadWriteValueListener<T> {

    fun onSuccess(message: String, value: T? = null)

    fun onFailure(message: String)

}