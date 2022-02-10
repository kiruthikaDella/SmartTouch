package com.voinismartiot.voni.api.model

import com.google.gson.annotations.SerializedName

data class FAQResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<FAQResponseData>? = null
) {
    override fun toString(): String {
        return "FAQResponse(status=$status, code=$code, message='$message', data=$data)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FAQResponse

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

data class FAQResponseData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("vTitle")
    var title: String,
    @SerializedName("vDescription")
    var description: String,
    @SerializedName("tiIsExpand")
    var isExpand: Int
) {
    override fun toString(): String {
        return "FAQResponseData(id='$id', title='$title', description='$description', isExpand=$isExpand)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FAQResponseData

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (isExpand != other.isExpand) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + isExpand
        return result
    }


}
