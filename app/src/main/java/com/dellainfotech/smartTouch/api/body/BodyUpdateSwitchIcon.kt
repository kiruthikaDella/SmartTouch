package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyUpdateSwitchIcon(
    @SerializedName("iDeviceSwitchId") var switchId: String,
    @SerializedName("vIcon") var icon: String
)