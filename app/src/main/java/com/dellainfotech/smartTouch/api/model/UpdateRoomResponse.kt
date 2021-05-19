package com.dellainfotech.smartTouch.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by Jignesh Dangar on 11-05-2021.
 */

data class UpdateRoomResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: UpdateRoomData? = null
)

@Parcelize
data class UpdateRoomData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("iUserId")
    var userId: String,
    @SerializedName("vRoomName")
    var roomName: String,
    @SerializedName("iRoomTypeId")
    var roomTypeId: String
) : Parcelable, Serializable
