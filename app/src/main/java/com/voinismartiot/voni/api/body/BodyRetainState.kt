package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyRetainState(
    @SerializedName("iDeviceId") var deviceId: String,
    @SerializedName("tiRetainState") var retainState: Int
)