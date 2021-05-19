package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyAddDevice(
    @SerializedName("vDeviceSerialNo") var deviceSerialNo: String,
    @SerializedName("iRoomId") var roomId: String,
    @SerializedName("vDeviceName") var deviceName: String
)