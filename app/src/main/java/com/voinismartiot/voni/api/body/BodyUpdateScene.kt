package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyUpdateScene(
    @SerializedName("iSceneId") var sceneId: String,
    @SerializedName("vSceneName") var sceneName: String,
    @SerializedName("vSceneTime") var sceneTime: String,
    @SerializedName("vSceneTimeZone") var sceneTimeZone: String,
    @SerializedName("vSceneInterval") var sceneInterval: String,
    @SerializedName("iSchedulerTime") var schedulerTime: Long,
    @SerializedName("vSceneIntervalValue") var sceneIntervalValue: ArrayList<String>,
    @SerializedName("scene") var scenes: ArrayList<BodyUpdateSceneData>
) {

    override fun toString(): String {
        return "BodyUpdateScene(sceneId='$sceneId', sceneName='$sceneName', sceneTime='$sceneTime', sceneTimeZone='$sceneTimeZone', sceneInterval='$sceneInterval', schedulerTime=$schedulerTime, sceneIntervalValue=$sceneIntervalValue, scenes=$scenes)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BodyUpdateScene

        if (sceneId != other.sceneId) return false
        if (sceneName != other.sceneName) return false
        if (sceneTime != other.sceneTime) return false
        if (sceneTimeZone != other.sceneTimeZone) return false
        if (sceneInterval != other.sceneInterval) return false
        if (schedulerTime != other.schedulerTime) return false
        if (sceneIntervalValue != other.sceneIntervalValue) return false
        if (scenes != other.scenes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sceneId.hashCode()
        result = 31 * result + sceneName.hashCode()
        result = 31 * result + sceneTime.hashCode()
        result = 31 * result + sceneTimeZone.hashCode()
        result = 31 * result + sceneInterval.hashCode()
        result = 31 * result + schedulerTime.hashCode()
        result = 31 * result + sceneIntervalValue.hashCode()
        result = 31 * result + scenes.hashCode()
        return result
    }


}

class BodyUpdateSceneData(
    @SerializedName("iRoomId") var roomId: String,
    @SerializedName("iDeviceId") var deviceId: String,
    @SerializedName("iSceneDetailId") var sceneDetailId: String? = null,
    @SerializedName("iDeviceSwitchId") var deviceSwitchId: String,
    @SerializedName("tiDeviceSwitchSettingValue") var deviceSwitchSettingValue: Int
) {

    override fun toString(): String {
        return "BodyUpdateSceneData(roomId='$roomId', deviceId='$deviceId', sceneDetailId=$sceneDetailId, deviceSwitchId='$deviceSwitchId', deviceSwitchSettingValue=$deviceSwitchSettingValue)"
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
        var result = roomId.hashCode()
        result = 31 * result + deviceId.hashCode()
        result = 31 * result + (sceneDetailId?.hashCode() ?: 0)
        result = 31 * result + deviceSwitchId.hashCode()
        result = 31 * result + deviceSwitchSettingValue
        return result
    }


}