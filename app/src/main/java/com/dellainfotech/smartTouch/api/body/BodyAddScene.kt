package com.dellainfotech.smartTouch.api.body

import com.google.gson.annotations.SerializedName

class BodyAddScene(
    @SerializedName("vSceneName") var sceneName: String,
    @SerializedName("vSceneTime") var sceneTime: String,
    @SerializedName("vSceneTimeZone") var sceneTimeZone: String,
    @SerializedName("vSceneInterval") var sceneInterval: String,
    @SerializedName("vSceneIntervalValue") var sceneIntervalValue: ArrayList<String>,
    @SerializedName("scene") var scenes: ArrayList<BodySceneData>
){
    override fun toString(): String {
        return "BodyScene(sceneName='$sceneName', sceneTime='$sceneTime', sceneInterval='$sceneInterval', scenes=$scenes)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BodyAddScene

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

class BodySceneData(
    @SerializedName("iRoomId") var roomId: String,
    @SerializedName("iDeviceId") var deviceId: String,
    @SerializedName("iDeviceSwitchId") var deviceSwitchId: String,
    @SerializedName("tiDeviceSwitchSettingValue") var deviceSwitchSettingValue: Int
){
    override fun toString(): String {
        return "BodySceneData(roomId='$roomId', deviceId='$deviceId', deviceSwitchId='$deviceSwitchId', deviceSwitchSettingValue=$deviceSwitchSettingValue)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BodySceneData

        if (roomId != other.roomId) return false
        if (deviceId != other.deviceId) return false
        if (deviceSwitchId != other.deviceSwitchId) return false
        if (deviceSwitchSettingValue != other.deviceSwitchSettingValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roomId.hashCode()
        result = 31 * result + deviceId.hashCode()
        result = 31 * result + deviceSwitchId.hashCode()
        result = 31 * result + deviceSwitchSettingValue
        return result
    }


}