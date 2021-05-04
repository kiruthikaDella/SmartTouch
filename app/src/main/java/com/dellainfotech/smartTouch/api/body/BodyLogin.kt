package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyLogin {

    @SerializedName("vEmail")
    var userEmail: String = ""

    @SerializedName("vPassword")
    var userPassword: String = ""

    @SerializedName("iMobileuuid")
    var userMobileUUID: String = ""

    constructor(userEmail: String, userPassword: String, userMobileUUID: String) {
        this.userEmail = userEmail
        this.userPassword = userPassword
        this.userMobileUUID = userMobileUUID
    }
}