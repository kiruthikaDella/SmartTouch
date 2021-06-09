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

    //MQTT
    const val COGNITO_POOL_ID = "us-west-1:a1e688ab-af4c-40f2-9185-f211edfab734"
    val MY_REGION = Regions.US_WEST_1
    const val KEYSTORE_NAME = "smart_touch"
    const val KEYSTORE_PASSWORD = "smart_touch"
    const val CERTIFICATE_ID = ""
    const val AWS_IOT_POLICY_NAME = "smart_touch_policy"
    const val CUSTOMER_SPECIFIC_ENDPOINT = "a3gjsr00gjuzw5-ats.iot.us-west-1.amazonaws.com"
    const val topicForExerciseName = ""

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

    //MQTT TOPIC
    const val AWS_DEVICE_ID = "{deviceid}"
    const val Control_Device_Switches = "/smarttouch/$AWS_DEVICE_ID/control/"


    //MQTT Parameter
    const val AWS_SWITCH_1 = "SW01"
    const val AWS_SWITCH_2 = "SW02"
    const val AWS_SWITCH_3 = "SW03"
    const val AWS_SWITCH_4 = "SW04"
    const val AWS_SWITCH_5 = "SW05"
    const val AWS_SWITCH_6 = "SW06"
    const val AWS_SWITCH_7 = "SW07"
    const val AWS_SWITCH_8 = "SW08"
    const val AWS_USB_PORT_A = "USB_PORT_A"
    const val AWS_USB_PORT_C = "USB_PORT_C"
    const val AWS_DIMMER = "DIMMER"
}