package com.voinismartiot.voni.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class GetRoomResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<GetRoomData>? = null
)

@Parcelize
data class GetRoomData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("iRoomTypeId")
    var roomTypeId: GetRoomTypeId? = null,
    @SerializedName("iUserId")
    var userId: String? = null,
    @SerializedName("vRoomName")
    var roomName: String,
    @SerializedName("tiRetainState")
    var retainState: Int
) : Parcelable, Serializable {
    override fun toString(): String {
        return "GetRoomData(id='$id', roomTypeId=$roomTypeId, userId='$userId', roomName='$roomName', retainState=$retainState)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GetRoomData

        if (id != other.id) return false
        if (roomTypeId != other.roomTypeId) return false
        if (userId != other.userId) return false
        if (roomName != other.roomName) return false
        if (retainState != other.retainState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (roomTypeId?.hashCode() ?: 0)
        result = 31 * result + (userId?.hashCode() ?: 0)
        result = 31 * result + roomName.hashCode()
        result = 31 * result + retainState
        return result
    }


}

@Parcelize
data class GetRoomTypeId(
    @SerializedName("_id")
    var id: String,
    @SerializedName("vName")
    var roomName: String,
    @SerializedName("vFilePath")
    var filePath: String,
    @SerializedName("vFile")
    var file: String? = null
) : Parcelable, Serializable {
    override fun toString(): String {
        return "GetRoomTypeId(id='$id', roomName='$roomName', filePath='$filePath', file='$file')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GetRoomTypeId

        if (id != other.id) return false
        if (roomName != other.roomName) return false
        if (filePath != other.filePath) return false
        if (file != other.file) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + roomName.hashCode()
        result = 31 * result + filePath.hashCode()
        result = 31 * result + (file?.hashCode() ?: 0)
        return result
    }


}
