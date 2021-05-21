package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyCustomizationLock(
    @SerializedName("iDeviceId") var deviceId: String,
    @SerializedName("tiIsLock") var isLock: Int
)