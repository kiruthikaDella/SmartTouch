package com.dellainfotech.smartTouch.api.model

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
    var userId: String,
    @SerializedName("vRoomName")
    var roomName: String,
    @SerializedName("vRoomType")
    var roomType: String
) : Parcelable, Serializable

@Parcelize
data class GetRoomTypeId(
    @SerializedName("_id")
    var id: String,
    @SerializedName("vName")
    var roomName: String
) : Parcelable, Serializable
