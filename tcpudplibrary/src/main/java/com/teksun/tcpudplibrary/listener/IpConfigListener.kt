package com.teksun.tcpudplibrary.listener

interface IpConfigListener {

    fun getSSID(ssid: String)

    fun getIpAddress(ip: String)

    fun getGateway(gateway: String)

    fun getNetMask(netmask: String)

    fun getMac(macAddress: String)
}