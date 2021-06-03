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
) {
    override fun toString(): String {
        return "AddDeviceResponse(status=$status, code=$code, message='$message', data=$data)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddDeviceResponse

        if (status != other.status) return false
        if (code != other.code) return false
        if (message != other.message) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + code
        result = 31 * result + message.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }


}

@Parcelize
data class GetDeviceData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("tiIsDeviceAvilable")
    var isDeviceAvailable: String,
    @SerializedName("iActiveSwitchCount")
    var activeSwitchCount: Int,
    @SerializedName("iUserId")
    var userId: String,
    @SerializedName("vDeviceSerialNo")
    var deviceSerialNo: String,
    @SerializedName("vDeviceName")
    var deviceName: String,
    @SerializedName("tiDeviceType")
    var deviceType: Int,
    @SerializedName("switchData")
    var switchData: ArrayList<DeviceSwitchData>? = null
) : Parcelable, Serializable {
    override fun toString(): String {
        return "GetDeviceData(id='$id', isDeviceAvailable='$isDeviceAvailable', activeSwitchCount=$activeSwitchCount, userId='$userId', deviceSerialNo='$deviceSerialNo', deviceName='$deviceName', deviceType=$deviceType, switchData=$switchData)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GetDeviceData

        if (id != other.id) return false
        if (isDeviceAvailable != other.isDeviceAvailable) return false
        if (activeSwitchCount != other.activeSwitchCount) return false
        if (userId != other.userId) return false
        if (deviceSerialNo != other.deviceSerialNo) return false
        if (deviceName != other.deviceName) return false
        if (deviceType != other.deviceType) return false
        if (switchData != other.switchData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + isDeviceAvailable.hashCode()
        result = 31 * result + activeSwitchCount
        result = 31 * result + userId.hashCode()
        result = 31 * result + deviceSerialNo.hashCode()
        result = 31 * result + deviceName.hashCode()
        result = 31 * result + deviceType
        result = 31 * result + (switchData?.hashCode() ?: 0)
        return result
    }


}

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
    @SerializedName("vDesc")
    var desc: String? = null,
    @SerializedName("tiSwitchStatus")
    var switchStatus: Int
) : Parcelable, Serializable {
    override fun toString(): String {
        return "DeviceSwitchData(id='$id', icon='$icon', typeOfSwitch=$typeOfSwitch, index='$index', name='$name', desc=$desc, switchStatus=$switchStatus)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceSwitchData

        if (id != other.id) return false
        if (icon != other.icon) return false
        if (typeOfSwitch != other.typeOfSwitch) return false
        if (index != other.index) return false
        if (name != other.name) return false
        if (desc != other.desc) return false
        if (switchStatus != other.switchStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + typeOfSwitch
        result = 31 * result + index.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (desc?.hashCode() ?: 0)
        result = 31 * result + switchStatus
        return result
    }


}
