package com.voinismartiot.voni.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class DeviceFeatureResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: DeviceFeatureData? = null
) {

    override fun toString(): String {
        return "DeviceFeatureResponse(status=$status, code=$code, message='$message', data=$data)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceFeatureResponse

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
data class DeviceFeatureData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("tiSleepMode")
    var sleepMode: Int,
    @SerializedName("tiNightMode")
    var nightMode: Int,
    @SerializedName("vSleepModeSecond")
    var sleepModeSecond: String? = null,
    @SerializedName("tiTime")
    var time: Int,
    @SerializedName("tiDate")
    var date: Int,
    @SerializedName("tiOutdoorMode")
    var outdoorMode: Int,
    @SerializedName("bTimeFormat")
    var timeFormat: Int,
    @SerializedName("tiWeatherReport")
    var weatherReport: Int,
    @SerializedName("tiRoomTemperature")
    var roomTemperature: Int,
    @SerializedName("bTemperatureUnit")
    var temperatureUnit: Int,
    @SerializedName("tiDisplayBrightnessMode")
    var displayBrightnessMode: String,
    @SerializedName("tiDisplayBrightnessValue")
    var displayBrightnessValue: String,
) : Parcelable, Serializable {

    override fun toString(): String {
        return "DeviceFeatureData(id='$id', sleepMode=$sleepMode, nightMode=$nightMode, sleepModeSecond=$sleepModeSecond, time=$time, date=$date, outdoorMode=$outdoorMode, timeFormat=$timeFormat, weatherReport=$weatherReport, roomTemperature=$roomTemperature, temperatureUnit=$temperatureUnit, displayBrightnessMode='$displayBrightnessMode', displayBrightnessValue='$displayBrightnessValue')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceFeatureData

        if (id != other.id) return false
        if (sleepMode != other.sleepMode) return false
        if (nightMode != other.nightMode) return false
        if (sleepModeSecond != other.sleepModeSecond) return false
        if (time != other.time) return false
        if (date != other.date) return false
        if (outdoorMode != other.outdoorMode) return false
        if (timeFormat != other.timeFormat) return false
        if (weatherReport != other.weatherReport) return false
        if (roomTemperature != other.roomTemperature) return false
        if (temperatureUnit != other.temperatureUnit) return false
        if (displayBrightnessMode != other.displayBrightnessMode) return false
        if (displayBrightnessValue != other.displayBrightnessValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + sleepMode
        result = 31 * result + nightMode
        result = 31 * result + (sleepModeSecond?.hashCode() ?: 0)
        result = 31 * result + time
        result = 31 * result + date
        result = 31 * result + outdoorMode
        result = 31 * result + timeFormat
        result = 31 * result + weatherReport
        result = 31 * result + roomTemperature
        result = 31 * result + temperatureUnit
        result = 31 * result + displayBrightnessMode.hashCode()
        result = 31 * result + displayBrightnessValue.hashCode()
        return result
    }


}
