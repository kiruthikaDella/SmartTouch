package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyUpdateSwitchName(
    @SerializedName("iDeviceSwitchId") var switchId: String,
    @SerializedName("vName") var switchName: String
)