package com.teksun.wifilibrary.listener

import android.net.wifi.ScanResult

public interface ScanResultListener {
    fun onScanResult(scanResult: List<ScanResult>)

    fun onFailure(message: String)
}