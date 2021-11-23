package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyUpdateSceneStatus(
    @SerializedName("isDeviceDisable") var isDeviceDisable: Int
)