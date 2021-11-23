package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyAddSubordinateUser(
    @SerializedName("vFullName") var fullName: String,
    @SerializedName("vEmail") var email: String
)