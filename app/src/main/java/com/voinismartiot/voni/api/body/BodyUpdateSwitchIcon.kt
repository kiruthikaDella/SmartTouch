package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyUpdateSwitchIcon(
    @SerializedName("iDeviceId") var deviceId: String,
    @SerializedName("iDeviceSwitchId") var switchId: String,
    @SerializedName("vIcon") var icon: String
)