package com.dellainfotech.smartTouch.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Jignesh Dangar on 11-05-2021.
 */
data class SubordinateUserResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<SubordinateUserData>? = null
)

data class SubordinateUserData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("iParentId")
    var parentId: String,
    @SerializedName("vFullName")
    var fullName: String,
    @SerializedName("vEmail")
    var email: String
)
