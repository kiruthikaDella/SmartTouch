package com.dellainfotech.smartTouch.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by Jignesh Dangar on 19-05-2021.
 */

data class OwnershipResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: OwnershipTransferData? = null
)

@Parcelize
data class OwnershipTransferData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("vEmail")
    var email: String,
    @SerializedName("vName")
    var name: String,
    @SerializedName("isEmailVerified")
    var isEmailVerified: Int
) : Parcelable, Serializable