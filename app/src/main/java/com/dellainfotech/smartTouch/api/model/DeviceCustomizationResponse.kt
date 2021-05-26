package com.dellainfotech.smartTouch.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by Jignesh Dangar on 19-05-2021.
 */

data class DeviceCustomizationResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: DeviceCustomizationData? = null
)

@Parcelize
data class DeviceCustomizationData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("vUploadImage")
    var uploadImage: String,
    @SerializedName("vScreenLayoutType")
    var screenLayoutType: String,
    @SerializedName("vScreenLayout")
    var screenLayout: String,
    @SerializedName("vSwitchName")
    var switchName: String,
    @SerializedName("iSwitchIconSize")
    var switchIconSize: String,
    @SerializedName("vTextStyle")
    var textStyle: String,
    @SerializedName("vTextColor")
    var textColor: String,
    @SerializedName("vTextSize")
    var textSize: String,
    @SerializedName("tiIsLock")
    var isLock: Int
) : Parcelable, Serializable
