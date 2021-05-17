package com.dellainfotech.smartTouch.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Jignesh Dangar on 12-05-2021.
 */

data class FAQResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var data: List<FAQResponseData>? = null
)

data class FAQResponseData(
    @SerializedName("_id")
    var id: String,
    @SerializedName("vTitle")
    var title: String,
    @SerializedName("vDescription")
    var description: String,
    @SerializedName("tiIsExpand")
    var isExpand: Int
)
