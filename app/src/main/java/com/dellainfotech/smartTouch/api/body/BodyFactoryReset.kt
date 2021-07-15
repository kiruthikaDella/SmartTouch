package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyFactoryReset(
    @SerializedName("iDeviceId") var deviceId: String,
    @SerializedName("tiDeviceType") var deviceType: String
)