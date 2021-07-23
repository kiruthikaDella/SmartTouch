package com.binjal.wifilibrary.listener

import android.net.wifi.WifiConfiguration

interface ConfigureWifiResultListener {
    fun onConfigureResult(configureResult: List<WifiConfiguration>)

    fun onFailure(message: String)
}