package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyUpdateUserProfile(
    @SerializedName("vFullName") var fullName: String? = null,
    @SerializedName("bPhoneNumber") var phoneNumber: String? = null
)