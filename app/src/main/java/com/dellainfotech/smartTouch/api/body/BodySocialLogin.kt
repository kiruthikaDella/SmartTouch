package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodySocialLogin(
    @SerializedName("iSocialId") var socialId: String,
    @SerializedName("iMobileuuid") var mobileUUID: String,
    @SerializedName("tiType") var type: String,
    @SerializedName("tiLoginType") var loginType: String,
    @SerializedName("vEmail") var userEmail: String
)