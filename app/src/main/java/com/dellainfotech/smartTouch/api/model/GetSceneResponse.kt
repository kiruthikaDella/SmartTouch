package com.dellainfotech.smartTouch.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by Jignesh Dangar on 03-06-2021.
 */

data class GetSceneResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: ArrayList<GetSceneData>? = null
){
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
    @SerializedName("vSceneInterval")
    var sceneInterval: String,
    @SerializedName("scene")
    var scene: ArrayList<Scene>? = null
): Parcelable, Serializable {
    override fun toString(): String {
        return "GetSceneData(id='$id', sceneName='$sceneName', sceneTime='$sceneTime', sceneInterval='$sceneInterval', scene=$scene)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GetSceneData

        if (id != other.id) return false
        if (sceneName != other.sceneName) return false
        if (sceneTime != other.sceneTime) return false
        if (sceneInterval != other.sceneInterval) return false
        if (scene != other.scene) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + sceneName.hashCode()
        result = 31 * result + sceneTime.hashCode()
        result = 31 * result + sceneInterval.hashCode()
        result = 31 * result + (scene?.hashCode() ?: 0)
        return result
    }


}

@Parcelize
data class Scene(
    @SerializedName("_id")
    var id: String,
    @SerializedName("iSceneId")
    var sceneId: String,
    @SerializedName("iRoomId")
    var roomId: GetRoomData? = null,
    @SerializedName("iDeviceId")
    var deviceId: GetDeviceData? = null,
    @SerializedName("iDeviceSwitchId")
    var deviceSwitchId: DeviceSwitchData? = null,
    @SerializedName("tiDeviceSwitchSettingValue")
    var deviceSwitchSettingValue: Int
): Parcelable, Serializable {
    override fun toString(): String {
        return "Scene(id='$id', sceneId='$sceneId', roomId=$roomId, deviceId=$deviceId, deviceSwitchId=$deviceSwitchId, deviceSwitchSettingValue=$deviceSwitchSettingValue)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Scene

        if (id != other.id) return false
        if (sceneId != other.sceneId) return false
        if (roomId != other.roomId) return false
        if (deviceId != other.deviceId) return false
        if (deviceSwitchId != other.deviceSwitchId) return false
        if (deviceSwitchSettingValue != other.deviceSwitchSettingValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + sceneId.hashCode()
        result = 31 * result + (roomId?.hashCode() ?: 0)
        result = 31 * result + (deviceId?.hashCode() ?: 0)
        result = 31 * result + (deviceSwitchId?.hashCode() ?: 0)
        result = 31 * result + deviceSwitchSettingValue
        return result
    }


}


