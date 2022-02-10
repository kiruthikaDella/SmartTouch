package com.voinismartiot.voni.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class OwnershipResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: OwnershipTransferData? = null
) {
    override fun toString(): String {
        return "OwnershipResponse(status=$status, code=$code, message='$message', data=$data)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OwnershipResponse

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
data class OwnershipTransferData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("vEmail")
    var email: String,
    @SerializedName("vName")
    var name: String,
    @SerializedName("isEmailVerified")
    var isEmailVerified: Int
) : Parcelable, Serializable {
    override fun toString(): String {
        return "OwnershipTransferData(id='$id', email='$email', name='$name', isEmailVerified=$isEmailVerified)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OwnershipTransferData

        if (id != other.id) return false
        if (email != other.email) return false
        if (name != other.name) return false
        if (isEmailVerified != other.isEmailVerified) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + isEmailVerified
        return result
    }


}