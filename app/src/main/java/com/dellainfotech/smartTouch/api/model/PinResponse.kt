package com.dellainfotech.smartTouch.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by Jignesh Dangar on 26-05-2021.
 */

data class PinResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: PinData? = null
)

@Parcelize
data class PinData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("iIsPinStatus")
    var isPinStatus: Int
) : Parcelable, Serializable
