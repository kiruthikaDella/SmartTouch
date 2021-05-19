package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyChangePassword(
    @SerializedName("vOldPassword") var oldPassword: String,
    @SerializedName("vNewPassword") var newPassword: String
)