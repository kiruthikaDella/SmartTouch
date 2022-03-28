package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyAddDevice(
    @SerializedName("vDeviceSerialNo") var deviceSerialNo: String,
    @SerializedName("iRoomId") var roomId: String,
    @SerializedName("vDeviceName") var deviceName: String,
    @SerializedName("vLatitude") var latitude: String,
    @SerializedName("vLongitude") var longitude: String
)