package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyUpdateDeviceName(
    @SerializedName("iDeviceId") var deviceId: String,
    @SerializedName("vDeviceName") var deviceName: String
)