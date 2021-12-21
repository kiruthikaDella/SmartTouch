package com.teksun.wifilibrary.listener

import android.net.wifi.WifiNetworkSuggestion

interface SavedNetworkResultListener {

    fun onSuccess(message: String? = null, networkId: Int? = null, suggestionList: ArrayList<WifiNetworkSuggestion>? = null)

    fun onFailure(message: String)
}