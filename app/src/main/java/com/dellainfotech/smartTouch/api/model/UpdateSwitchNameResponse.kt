package com.dellainfotech.smartTouch.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Jignesh Dangar on 19-05-2021.
 */

data class UpdateSwitchNameResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: DeviceSwitchData? = null
)
