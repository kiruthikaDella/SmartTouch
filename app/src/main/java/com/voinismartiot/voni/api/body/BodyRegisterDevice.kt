package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

data class BodyRegisterDevice(
    @SerializedName("vDeviceSerialNo")
    val deviceSerialNum: String,
    @SerializedName("iRoomId")
    val roomId: String,
    @SerializedName("vDeviceName")
    val deviceName: String,
    @SerializedName("tiDeviceType")
    val deviceType: String,
    @SerializedName("vWifiSSID")
    val wifiSSID: String,
    @SerializedName("vPassword")
    val password: String,
    @SerializedName("vMacImei")
    val macImei: String,
    @SerializedName("vProductGroup")
    val productGroup: String,
    @SerializedName("vFirmwareVersion")
    val firmwareVersion: String,
    @SerializedName("vManufactureDate")
    val manufactureDate: String,
    @SerializedName("vDesc")
    val desc: String,
    @SerializedName("vUniqueId")
    val uniqueId: String
)
