package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyPinStatus(
    @SerializedName("iIsPinStatus") var isPinStatus: Int,
)