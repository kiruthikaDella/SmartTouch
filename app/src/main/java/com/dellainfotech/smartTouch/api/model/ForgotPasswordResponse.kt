package com.dellainfotech.smartTouch.api.model

import com.google.gson.annotations.SerializedName

data class ForgotPasswordResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String
)
