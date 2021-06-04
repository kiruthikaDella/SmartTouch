package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyUpdateScene(
    @SerializedName("iSceneId") var sceneId: String,
    @SerializedName("vSceneName") var sceneName: String,
    @SerializedName("vSceneTime") var sceneTime: String,
    @SerializedName("vSceneInterval") var sceneInterval: String,
    @SerializedName("scene") var scenes: ArrayList<BodyUpdateSceneData>
){
    override fun toString(): String {
        return "BodyUpdateScene(sceneName='$sceneName', sceneTime='$sceneTime', sceneInterval='$sceneInterval', scenes=$scenes)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BodyUpdateScene

        if (sceneName != other.sceneName) return false
        if (sceneTime != other.sceneTime) return false
        if (sceneInterval != other.sceneInterval) return false
        if (scenes != other.scenes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sceneName.hashCode()
        result = 31 * result + sceneTime.hashCode()
        result = 31 * result + sceneInterval.hashCode()
        result = 31 * result + scenes.hashCode()
        return result
    }


}

class BodyUpdateSceneData(
    @SerializedName("iRoomId") var roomId: String? = null,
    @SerializedName("iDeviceId") var deviceId: String? = null,
    @SerializedName("iSceneDetailId") var sceneDetailId: String? = null,
    @SerializedName("iDeviceSwitchId") var deviceSwitchId: String,
    @SerializedName("tiDeviceSwitchSettingValue") var deviceSwitchSettingValue: Int
){

    override fun toString(): String {
        return "BodyUpdateSceneData(roomId=$roomId, deviceId=$deviceId, sceneDetailId='$sceneDetailId', deviceSwitchId='$deviceSwitchId', deviceSwitchSettingValue=$deviceSwitchSettingValue)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BodyUpdateSceneData

        if (roomId != other.roomId) return false
        if (deviceId != other.deviceId) return false
        if (sceneDetailId != other.sceneDetailId) return false
        if (deviceSwitchId != other.deviceSwitchId) return false
        if (deviceSwitchSettingValue != other.deviceSwitchSettingValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roomId?.hashCode() ?: 0
        result = 31 * result + (deviceId?.hashCode() ?: 0)
        result = 31 * result + sceneDetailId.hashCode()
        result = 31 * result + deviceSwitchId.hashCode()
        result = 31 * result + deviceSwitchSettingValue
        return result
    }


}