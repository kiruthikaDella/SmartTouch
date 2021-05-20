package com.dellainfotech.smartTouch.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by Jignesh Dangar on 19-05-2021.
 */

data class DeviceFeatureResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: DeviceFeatureData? = null
)

@Parcelize
data class DeviceFeatureData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("tiSleepMode")
    var sleepMode: Int,
    @SerializedName("tiNightMode")
    var nightMode: Int,
    @SerializedName("tiTime")
    var time: Int,
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
) : Parcelable, Serializable
