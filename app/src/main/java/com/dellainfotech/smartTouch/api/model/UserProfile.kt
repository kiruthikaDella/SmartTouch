package com.dellainfotech.smartTouch.api.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("_id")
    var iUserId: String? = null,
    @SerializedName("vFullName")
    var vFullName: String? = null,
    @SerializedName("vUserName")
    var vUserName: String? = null,
    @SerializedName("vEmail")
    var vEmail: String? = null,
    @SerializedName("bPhoneNumber")
    var bPhoneNumber: String? = null,
    @SerializedName("iIsPinStatus")
    var iIsPinStatus: Int? = null
)