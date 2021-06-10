package com.dellainfotech.smartTouch.common.utils

import com.amazonaws.regions.Regions

/**
 * Created by Jignesh Dangar on 19-04-2021.
 */
object Constants {

    var COMMON_DIALOG_WIDTH = 0.8.toFloat()
    var COMMON_DIALOG_HEIGHT = 0.9.toFloat()

    const val API_SUCCESS_CODE = 200
    const val API_FAILURE_CODE = 401

    const val REQUEST_CAMERA_PERMISSION = 101
    const val REQUEST_IMAGE_CAPTURE = 102
    const val REQUEST_GALLERY_IMAGE = 103
    const val REQUEST_OPEN_SETTINGS = 104

    const val DEVICE_TYPE_EIGHT = 8
    const val DEVICE_TYPE_FOUR = 4

    const val EXTRA_FILE_PATH = "extra.file_path"

    const val DEFAULT_CONTROL_MODE_STATUS = false
    const val isControlModePinned = "isControlModePinned"
    const val DEFAULT_DEVICE_CUSTOMIZATION_LOCK_STATUS = false
    const val isDeviceCustomizationLocked = "isDeviceCustomizationLocked"
    const val DEFAULT_SCREEN_LAYOUT_LOCK_STATUS = false
    const val isScreenLayoutLocked = "isScreenLayoutLocked"
    const val DEFAULT_SWITCH_ICONS_LOCK_STATUS = false
    const val isSwitchIconsLocked = "isSwitchIconsLocked"

    const val PASSWORD_LENGTH = 6
    const val SOCIAL_LOGIN = "2"

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

    const val MOBILE_UUID = "mobile_uuid"
    const val DEFAULT_REMEMBER_STATUS = false
    const val IS_REMEMBER = "is_remember"
    const val IS_LOGGED_IN = "is_logged_in"


}