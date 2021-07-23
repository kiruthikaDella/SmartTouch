package com.teksun.tcpudplibrary.listener

interface ConnectResultListener<T> {

    fun onSuccess(message: String, hashMap: HashMap<String, T>?)

    fun onFailure(message: String)
}