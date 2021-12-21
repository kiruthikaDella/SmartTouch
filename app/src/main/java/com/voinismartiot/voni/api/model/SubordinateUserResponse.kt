package com.voinismartiot.voni.api.model

import com.google.gson.annotations.SerializedName

data class SubordinateUserResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<SubordinateUserData>? = null
) {
    override fun toString(): String {
        return "SubordinateUserResponse(status=$status, code=$code, message='$message', data=$data)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SubordinateUserResponse

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

data class SubordinateUserData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("iParentId")
    var parentId: String,
    @SerializedName("vFullName")
    var fullName: String,
    @SerializedName("vEmail")
    var email: String
) {
    override fun toString(): String {
        return "SubordinateUserData(id='$id', parentId='$parentId', fullName='$fullName', email='$email')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SubordinateUserData

        if (id != other.id) return false
        if (parentId != other.parentId) return false
        if (fullName != other.fullName) return false
        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + parentId.hashCode()
        result = 31 * result + fullName.hashCode()
        result = 31 * result + email.hashCode()
        return result
    }


}
