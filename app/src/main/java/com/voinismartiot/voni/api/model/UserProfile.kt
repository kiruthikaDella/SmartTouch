package com.voinismartiot.voni.api.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("_id")
    var iUserId: String? = null,
    @SerializedName("vFullName")
    var vFullName: String? = null,
    @SerializedName("vUserName")
    var vUserName: String? = null,
    @SerializedName("vEmail")
    var vEmail: String? = null,
    @SerializedName("iSocialId")
    var socialId: String? = null,
    @SerializedName("bPhoneNumber")
    var bPhoneNumber: String? = null,
    @SerializedName("user_role")
    var userRole: String? = null,
    @SerializedName("iIsPinStatus")
    var iIsPinStatus: Int? = null
) {

    override fun toString(): String {
        return "UserProfile(iUserId=$iUserId, vFullName=$vFullName, vUserName=$vUserName, vEmail=$vEmail, socialId=$socialId, bPhoneNumber=$bPhoneNumber, userRole=$userRole, iIsPinStatus=$iIsPinStatus)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserProfile

        if (iUserId != other.iUserId) return false
        if (vFullName != other.vFullName) return false
        if (vUserName != other.vUserName) return false
        if (vEmail != other.vEmail) return false
        if (socialId != other.socialId) return false
        if (bPhoneNumber != other.bPhoneNumber) return false
        if (userRole != other.userRole) return false
        if (iIsPinStatus != other.iIsPinStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iUserId?.hashCode() ?: 0
        result = 31 * result + (vFullName?.hashCode() ?: 0)
        result = 31 * result + (vUserName?.hashCode() ?: 0)
        result = 31 * result + (vEmail?.hashCode() ?: 0)
        result = 31 * result + (socialId?.hashCode() ?: 0)
        result = 31 * result + (bPhoneNumber?.hashCode() ?: 0)
        result = 31 * result + (userRole?.hashCode() ?: 0)
        result = 31 * result + (iIsPinStatus ?: 0)
        return result
    }


}