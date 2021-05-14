package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyUpdateUserProfile(
    @SerializedName("vUserName") var userName: String,
    @SerializedName("bPhoneNumber") var phoneNumber: String,
    @SerializedName("vPassword") var password: String,
    @SerializedName("otName") var otName: String,
    @SerializedName("otEmail") var otEmail: String
)