package com.voinismartiot.voni.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class IconListResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<IconListData>? = null
) {
    override fun toString(): String {
        return "IconListResponse(status=$status, code=$code, message='$message', data=$data)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IconListResponse

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
data class IconListData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("vIconFile")
    var iconFile: String,
    @SerializedName("vIconName")
    var iconName: String,
    @SerializedName("vIcon")
    var icon: String
) : Parcelable, Serializable {
    override fun toString(): String {
        return "IconListData(id='$id', iconFile='$iconFile', iconName='$iconName', icon='$icon')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IconListData

        if (id != other.id) return false
        if (iconFile != other.iconFile) return false
        if (iconName != other.iconName) return false
        if (icon != other.icon) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + iconFile.hashCode()
        result = 31 * result + iconName.hashCode()
        result = 31 * result + icon.hashCode()
        return result
    }


}
