package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyForgotPassword {

    @SerializedName("vEmail")
    var userEmail: String = ""

    constructor(userEmail: String) {
        this.userEmail = userEmail
    }
}