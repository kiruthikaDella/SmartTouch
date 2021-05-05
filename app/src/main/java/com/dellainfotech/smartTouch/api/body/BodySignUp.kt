package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodySignUp {

    @SerializedName("vFullName")
    var userFullName: String = ""

    @SerializedName("vUserName")
    var userName: String = ""

    @SerializedName("vEmail")
    var userEmail: String = ""

    @SerializedName("vPassword")
    var userPassword: String = ""

    @SerializedName("vConfirmPassword")
    var userConfirmPassword: String = ""

    @SerializedName("bPhoneNumber")
    var userPhoneNumber: String = ""

    constructor(
        userFullName: String,
        userName: String,
        userEmail: String,
        userPassword: String,
        userConfirmPassword: String,
        userPhoneNumber: String
    ) {
        this.userFullName = userFullName
        this.userName = userName
        this.userEmail = userEmail
        this.userPassword = userPassword
        this.userConfirmPassword = userConfirmPassword
        this.userPhoneNumber = userPhoneNumber
    }
}