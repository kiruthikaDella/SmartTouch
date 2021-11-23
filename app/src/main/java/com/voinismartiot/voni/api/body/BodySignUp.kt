package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodySignUp(
    @SerializedName("vFullName") var userFullName: String,
    @SerializedName("vUserName") var userName: String,
    @SerializedName("vEmail") var userEmail: String,
    @SerializedName("vPassword") var userPassword: String,
    @SerializedName("vConfirmPassword") var userConfirmPassword: String,
    @SerializedName("tiLoginType") var loginType: String,
    @SerializedName("bPhoneNumber") var userPhoneNumber: String
)