package com.voinismartiot.voni.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

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
) : Parcelable, Serializable {

    override fun toString(): String {
        return "DeviceCustomizationData(id='$id', uploadImage='$uploadImage', screenLayoutType='$screenLayoutType', screenLayout='$screenLayout', switchName='$switchName', switchIconSize='$switchIconSize', textStyle='$textStyle', textColor='$textColor', textSize='$textSize', isLock=$isLock)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceCustomizationData

        if (id != other.id) return false
        if (uploadImage != other.uploadImage) return false
        if (screenLayoutType != other.screenLayoutType) return false
        if (screenLayout != other.screenLayout) return false
        if (switchName != other.switchName) return false
        if (switchIconSize != other.switchIconSize) return false
        if (textStyle != other.textStyle) return false
        if (textColor != other.textColor) return false
        if (textSize != other.textSize) return false
        if (isLock != other.isLock) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + uploadImage.hashCode()
        result = 31 * result + screenLayoutType.hashCode()
        result = 31 * result + screenLayout.hashCode()
        result = 31 * result + switchName.hashCode()
        result = 31 * result + switchIconSize.hashCode()
        result = 31 * result + textStyle.hashCode()
        result = 31 * result + textColor.hashCode()
        result = 31 * result + textSize.hashCode()
        result = 31 * result + isLock
        return result
    }


}
