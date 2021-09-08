package com.binjal.wifilibrary.listener

import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo

public interface WifiInfoResultListener {
    fun onWifiInfoResult(wifiInfoResult: WifiInfo)
}