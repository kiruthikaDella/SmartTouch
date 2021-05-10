package com.dellainfotech.smartTouch.api.model

data class UserProfile(
    var iUserId: String? = null,
    var vFullName: String? = null,
    var vUserName: String? = null,
    var vEmail: String? = null,
    var vPassword: String? = null,
    var bPhoneNumber: String? = null
)