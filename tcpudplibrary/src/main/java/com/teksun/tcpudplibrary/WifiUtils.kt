package com.teksun.tcpudplibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings

@SuppressLint("StaticFieldLeak")
object WifiUtils {

    private var mContext: Context? = null
    private var mWifiManager: WifiManager? = null

    fun checkWifiStatus(mContext: Context): Boolean {
        this.mContext = mContext
        mWifiManager = mContext.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return mWifiManager?.isWifiEnabled!!
    }

    fun enableWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            (mContext as Activity).startActivityForResult(panelIntent, 0)
        } else {
            mWifiManager?.apply { isWifiEnabled = true }
        }
    }
}