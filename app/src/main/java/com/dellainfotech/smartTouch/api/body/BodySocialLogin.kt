package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodySocialLogin {

    @SerializedName("iSocialId")
    var socialId: String = ""

    @SerializedName("iMobileuuid")
    var mobileUUID: String = ""

    @SerializedName("tiType")
    var type: String = ""

    @SerializedName("vEmail")
    var userEmail: String = ""

    constructor(socialId: String, mobileUUID: String, type: String, userEmail: String) {
        this.socialId = socialId
        this.mobileUUID = mobileUUID
        this.type = type
        this.userEmail = userEmail
    }
}