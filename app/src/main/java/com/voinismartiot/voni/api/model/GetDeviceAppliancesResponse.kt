package com.voinismartiot.voni.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class GetDeviceAppliancesResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<DeviceAppliances>? = null
) {

    override fun toString(): String {
        return "GetDeviceAppliancesResponse(status=$status, code=$code, message='$message', data=$data)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GetDeviceAppliancesResponse

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
data class DeviceAppliances(
    @SerializedName("vMessage")
    var message: String,
    @SerializedName("_id")
    var id: String,
    @SerializedName("vTitle")
    var title: String,
    @SerializedName("iCreatedAt")
    var createdAt: String,
    @SerializedName("iUpdatedAt")
    var updatedAt: String,
    @SerializedName("vGroupType")
    var groupType: String,
) : Parcelable, Serializable {

    override fun toString(): String {
        return "DeviceAppliances(message='$message', id='$id', title='$title', createdAt='$createdAt', updatedAt='$updatedAt', groupType='$groupType')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceAppliances

        if (message != other.message) return false
        if (id != other.id) return false
        if (title != other.title) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false
        if (groupType != other.groupType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + groupType.hashCode()
        return result
    }


}

