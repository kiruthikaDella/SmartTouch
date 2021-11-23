package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

data class BodyRegisterDevice(
    @SerializedName("vDeviceSerialNo")
    val deviceSerialNum: String,
    @SerializedName("iRoomId")
    val roomId: String,
    @SerializedName("vDeviceName")
    val deviceName: String,
    @SerializedName("vWifiSSID")
    val wifiSSID: String,
    @SerializedName("vPassword")
    val password: String,
    @SerializedName("vMacImei")
    val macImei: String,
    @SerializedName("vProductGroup")
    val productGroup: String
)
