package com.voinismartiot.voni.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class GetSceneResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: ArrayList<GetSceneData>? = null
) {
    override fun toString(): String {
        return "GetSceneResponse(status=$status, code=$code, message='$message', data=$data)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GetSceneResponse

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
data class GetSceneData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("vSceneName")
    var sceneName: String,
    @SerializedName("vSceneTime")
    var sceneTime: String,
    @SerializedName("iSchedulerTime")
    var schedulerTime: Long,
    @SerializedName("vSceneInterval")
    var sceneInterval: String,
    @SerializedName("isDeviceDisable")
    var isDeviceDisable: Int,
    @SerializedName("vSceneIntervalValue")
    var sceneIntervalValue: ArrayList<String>? = null,
    @SerializedName("iUserId")
    var userId: String,
    @SerializedName("scene")
    var scene: ArrayList<Scene>? = null
) : Parcelable, Serializable {

    override fun toString(): String {
        return "GetSceneData(id='$id', sceneName='$sceneName', sceneTime='$sceneTime', schedulerTime=$schedulerTime, sceneInterval='$sceneInterval', isDeviceDisable=$isDeviceDisable, sceneIntervalValue=$sceneIntervalValue, userId='$userId', scene=$scene)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GetSceneData

        if (id != other.id) return false
        if (sceneName != other.sceneName) return false
        if (sceneTime != other.sceneTime) return false
        if (schedulerTime != other.schedulerTime) return false
        if (sceneInterval != other.sceneInterval) return false
        if (isDeviceDisable != other.isDeviceDisable) return false
        if (sceneIntervalValue != other.sceneIntervalValue) return false
        if (userId != other.userId) return false
        if (scene != other.scene) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + sceneName.hashCode()
        result = 31 * result + sceneTime.hashCode()
        result = 31 * result + schedulerTime.hashCode()
        result = 31 * result + sceneInterval.hashCode()
        result = 31 * result + isDeviceDisable
        result = 31 * result + (sceneIntervalValue?.hashCode() ?: 0)
        result = 31 * result + userId.hashCode()
        result = 31 * result + (scene?.hashCode() ?: 0)
        return result
    }


}

@Parcelize
data class Scene(
    @SerializedName("_id")
    var id: String,
    @SerializedName("iRoomId")
    var roomId: RoomId? = null,
    @SerializedName("iDeviceId")
    var deviceId: DeviceId? = null,
    @SerializedName("iDeviceSwitchId")
    var deviceSwitchId: DeviceSwitchId? = null,
    @SerializedName("tiDeviceSwitchSettingValue")
    var deviceSwitchSettingValue: Int
) : Parcelable, Serializable {

    override fun toString(): String {
        return "Scene(id='$id', roomData=$roomId, deviceData=$deviceId, switchData=$deviceSwitchId, deviceSwitchSettingValue=$deviceSwitchSettingValue)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Scene

        if (id != other.id) return false
        if (roomId != other.roomId) return false
        if (deviceId != other.deviceId) return false
        if (deviceSwitchId != other.deviceSwitchId) return false
        if (deviceSwitchSettingValue != other.deviceSwitchSettingValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (roomId?.hashCode() ?: 0)
        result = 31 * result + (deviceId?.hashCode() ?: 0)
        result = 31 * result + (deviceSwitchId?.hashCode() ?: 0)
        result = 31 * result + deviceSwitchSettingValue
        return result
    }


}

@Parcelize
data class RoomId(
    @SerializedName("_id")
    var id: String,
    @SerializedName("vRoomName")
    var roomName: String
) : Parcelable, Serializable {

    override fun toString(): String {
        return "RoomId(id='$id', roomName='$roomName')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoomId

        if (id != other.id) return false
        if (roomName != other.roomName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + roomName.hashCode()
        return result
    }


}

@Parcelize
data class DeviceId(
    @SerializedName("_id")
    var id: String,
    @SerializedName("vDeviceName")
    var deviceName: String
) : Parcelable, Serializable {

    override fun toString(): String {
        return "DeviceId(id='$id', deviceName='$deviceName')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceId

        if (id != other.id) return false
        if (deviceName != other.deviceName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + deviceName.hashCode()
        return result
    }

}

@Parcelize
data class DeviceSwitchId(
    @SerializedName("_id")
    var id: String,
    @SerializedName("vName")
    var switchName: String
) : Parcelable, Serializable {

    override fun toString(): String {
        return "DeviceSwitchId(id='$id', switchName='$switchName')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceSwitchId

        if (id != other.id) return false
        if (switchName != other.switchName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + switchName.hashCode()
        return result
    }


}



