package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodySubordinateUser(
    @SerializedName("vFullName") var fullName: String,
    @SerializedName("vEmail") var email: String
)