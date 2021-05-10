package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyLogout {

    @SerializedName("iMobileuuid")
    var userMobileUUID: String = ""

    constructor(userMobileUUID: String) {
        this.userMobileUUID = userMobileUUID
    }
}