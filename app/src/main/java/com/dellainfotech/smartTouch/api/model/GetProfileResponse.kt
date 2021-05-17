package com.dellainfotech.smartTouch.api.model

import com.google.gson.annotations.SerializedName

data class GetProfileResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: UserProfile? = null
)
