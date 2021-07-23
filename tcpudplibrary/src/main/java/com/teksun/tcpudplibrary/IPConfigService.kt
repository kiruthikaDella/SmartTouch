package com.teksun.tcpudplibrary

import android.content.Context
import android.net.wifi.WifiManager
import com.teksun.tcpudplibrary.listener.IpConfigListener

object IPConfigService {
    /**
     * get all wifi config
     * Get dhcpInfo, ssid, ipaddress, gateway, netmask and mac using wifi manager
     * @param context - pass context for initialize wifi manager
     * @param ipConfigListener - listener for used to transfer wifi data to activity
     * @see IpConfigListener
     */
    fun getWifiIpConfig(context: Context, ipConfigListener: IpConfigListener) {
        val wifiManager = context.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val dhcpInfo = wifiManager.dhcpInfo

        val connectionWifiInfo = wifiManager.connectionInfo
        
        ipConfigListener.getSSID(connectionWifiInfo.ssid)

        ipConfigListener.getIpAddress(intToIp(connectionWifiInfo.ipAddress).toString())

        ipConfigListener.getGateway(intToIp(dhcpInfo.gateway).toString())

        ipConfigListener.getNetMask(intToIp(dhcpInfo.netmask).toString())

        ipConfigListener.getMac(connectionWifiInfo.macAddress)
    }

    fun getIPAddress(context: Context): String {
        val wifiManager = context.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectionWifiInfo = wifiManager.connectionInfo
        return intToIp(connectionWifiInfo.ipAddress).toString()
    }

    private fun intToIp(addr: Int): String? {
        var address = addr
        return (address and 0xFF).toString() + "." +
                (8.let { address = address ushr it; address } and 0xFF) + "." +
                (8.let { address = address ushr it; address } and 0xFF) + "." +
                (8.let { address = address ushr it; address } and 0xFF)
    }
}