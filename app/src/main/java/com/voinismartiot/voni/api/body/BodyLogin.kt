package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyLogin(
    @SerializedName("vEmail") var userEmail: String,
    @SerializedName("vPassword") var userPassword: String,
    @SerializedName("iMobileuuid") var userMobileUUID: String,
    @SerializedName("vFcmToken") var fcmToken: String
)