package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyOwnership(
    @SerializedName("vEmail") var email: String,
    @SerializedName("vName") var name: String
)