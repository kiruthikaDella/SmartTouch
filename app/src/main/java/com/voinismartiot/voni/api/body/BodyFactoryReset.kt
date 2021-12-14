package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyFactoryReset(
    @SerializedName("iDeviceId") var deviceId: String,
    @SerializedName("tiDeviceType") var deviceType: String,
    @SerializedName("vProductGroup") var productGroup: String
)