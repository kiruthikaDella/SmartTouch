package com.voinismartiot.voni.common.utils

object Constants {

    const val SHARED_PREF = "smarttouch_preference"
    const val LOGGED_IN_EMAIL = "logged_in_email"
    const val LOGGED_IN_PASSWORD = "logged_in_password"
    const val LOGGED_IN_TYPE = "logged_in_type"
    const val IS_REMEMBER = "is_remember"

    var COMMON_DIALOG_WIDTH = 0.8.toFloat()
    var COMMON_DIALOG_HEIGHT = 0.9.toFloat()

    const val SLEEP_MODE_LIMIT_MAX = 600
    const val SLEEP_MODE_LIMIT_MIN = 5

    const val SYNC_DELAY = 5000L
    const val PING_HOLE_DELAY = 3000L
    const val OUTDOOR_MODE_DELAY = 3000L
    const val OUTDOOR_MODE_API_DELAY = 1000L

    const val API_SUCCESS_CODE = 200
    const val API_FAILURE_CODE = 400

    const val REQUEST_CAMERA_PERMISSION = 101
    const val REQUEST_IMAGE_CAPTURE = 102
    const val REQUEST_GALLERY_IMAGE = 103
    const val REQUEST_OPEN_SETTINGS = 104

    const val REQUEST_WIFI_CODE = 105

    const val REQUEST_GPS_CODE = 106

    const val DEVICE_TYPE_EIGHT = 8
    const val DEVICE_TYPE_FOUR = 4
    const val DEVICE_TYPE_SIX = 6
    const val DEVICE_TYPE_ONE = 1

    const val PRODUCT_SMART_TOUCH = "smartouch"
    const val PRODUCT_SMART_ACK = "smartack"
    const val PRODUCT_SMART_AP = "smartap"

    const val EXTRA_FILE_PATH = "extra.file_path"

    const val DEFAULT_CONTROL_MODE_STATUS = false
    const val isControlModePinned = "isControlModePinned"
    const val DEFAULT_DEVICE_CUSTOMIZATION_LOCK_STATUS = false
    const val isDeviceCustomizationLocked = "isDeviceCustomizationLocked"

    const val PASSWORD_LENGTH = 6
    const val SOCIAL_LOGIN = "2"
    const val LOGIN_TYPE = "login_type"
    const val LOGIN_TYPE_NORMAL = "Normal"
    const val LOGIN_TYPE_GOOGLE = "Google"
    const val LOGIN_TYPE_FACEBOOK = "Facebook"
    const val IS_MASTER_USER = "is_master_user"
    const val MASTER_USER = "Master User"
    const val SUBORDINATE_USER = "subordinate"

    //Swagger
    const val SECRET_KEY = "PXGuKjNtjzLiyLRE0GzykvGcaZO5uWE0"
    const val PRIVATE_KEY = "4AF8AC8E124B6484BDDC5A9EB3D49"

    const val USER_ID = "user_id"
    const val USER_FULL_NAME = "user_full_name"
    const val USERNAME = "username"
    const val USER_EMAIL = "user_email"
    const val USER_PHONE_NUMBER = "user_phone_number"
    const val SOCIAL_ID = "social_id"
    const val ACCESS_TOKEN = "access_token"
    const val FCM_TOKEN = "fcm_token"

    const val MOBILE_UUID = "mobile_uuid"
    const val DEFAULT_REMEMBER_STATUS = false
    const val IS_LOGGED_IN = "is_logged_in"

    const val REQUEST_FOR = "requestFor"
    const val GET_DEVICE_INFO = "getDeviceInfo"


}