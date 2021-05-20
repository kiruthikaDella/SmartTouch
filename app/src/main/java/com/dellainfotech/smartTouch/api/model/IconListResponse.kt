package com.dellainfotech.smartTouch.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by Jignesh Dangar on 19-05-2021.
 */

data class IconListResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<IconListData>? = null
)

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
) : Parcelable, Serializable
