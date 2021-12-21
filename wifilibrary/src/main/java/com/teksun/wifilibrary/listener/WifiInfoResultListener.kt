package com.teksun.wifilibrary.listener

import android.net.wifi.WifiInfo

public interface WifiInfoResultListener {
    fun onWifiInfoResult(wifiInfoResult: WifiInfo)
}