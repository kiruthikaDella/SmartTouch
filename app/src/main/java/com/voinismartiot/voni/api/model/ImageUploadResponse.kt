package com.voinismartiot.voni.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Jignesh Dangar on 19-05-2021.
 */

data class ImageUploadResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: ImageUploadData? = null
) {

    override fun toString(): String {
        return "ImageUploadResponse(status=$status, code=$code, message='$message', data=$data)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageUploadResponse

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


data class ImageUploadData(
    @SerializedName("vUploadImage")
    var uploadImage: String
){
    override fun toString(): String {
        return "ImageUploadData(uploadImage='$uploadImage')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageUploadData

        if (uploadImage != other.uploadImage) return false

        return true
    }

    override fun hashCode(): Int {
        return uploadImage.hashCode()
    }


}
