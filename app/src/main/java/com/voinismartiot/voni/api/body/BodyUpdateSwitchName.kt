package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyUpdateSwitchName(
    @SerializedName("iDeviceId") var deviceId: String,
    @SerializedName("iDeviceSwitchId") var switchId: String,
    @SerializedName("vName") var switchName: String
)