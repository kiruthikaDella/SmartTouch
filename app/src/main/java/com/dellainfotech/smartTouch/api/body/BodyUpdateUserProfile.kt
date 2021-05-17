package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyUpdateUserProfile(
    @SerializedName("vUserName") var userName: String? = null,
    @SerializedName("bPhoneNumber") var phoneNumber: String? = null,
    @SerializedName("vPassword") var password: String? = null,
    @SerializedName("otName") var otName: String? = null,
    @SerializedName("otEmail") var otEmail: String? = null
)