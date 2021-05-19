package com.dellainfotech.smartTouch.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by Jignesh Dangar on 19-05-2021.
 */

data class AddDeviceResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: GetDeviceData? = null
)

@Parcelize
data class GetDeviceData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("tiIsDeviceAvilable")
    var isDeviceAvailable: String,
    @SerializedName("iUserId")
    var userId: String,
    @SerializedName("vDeviceSerialNo")
    var deviceSerialNo: String,
    @SerializedName("vDeviceName")
    var deviceName: String,
    @SerializedName("tiDeviceType")
    var deviceType: Int,
    @SerializedName("switchData")
    var switchData: List<DeviceSwitchData>? = null
) : Parcelable, Serializable

@Parcelize
data class DeviceSwitchData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("vIcon")
    var icon: String,
    @SerializedName("vTypeOfSwitch")
    var typeOfSwitch: Int,
    @SerializedName("vIndex")
    var index: String,
    @SerializedName("vName")
    var name: String,
    @SerializedName("tiSwitchStatus")
    var switchStatus: Int
) : Parcelable, Serializable
