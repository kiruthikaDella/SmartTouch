package com.dellainfotech.smartTouch.common.utils

import com.amazonaws.regions.Regions

object MQTTConstants {

    //MQTT
    const val COGNITO_POOL_ID = "us-west-1:a1e688ab-af4c-40f2-9185-f211edfab734"
    val MY_REGION = Regions.US_WEST_1
    const val KEYSTORE_NAME = "smart_touch"
    const val KEYSTORE_PASSWORD = "smart_touch"
    const val CERTIFICATE_ID = ""
    const val AWS_IOT_POLICY_NAME = "smart_touch_policy"
    const val CUSTOMER_SPECIFIC_ENDPOINT = "a3gjsr00gjuzw5-ats.iot.us-west-1.amazonaws.com"
    const val topicForExerciseName = ""

    //MQTT TOPIC
    const val AWS_DEVICE_ID = "{deviceid}"
    const val DEVICE_STATUS = "/smarttouch/$AWS_DEVICE_ID/status/"
    const val CONTROL_DEVICE_SWITCHES = "/smarttouch/$AWS_DEVICE_ID/control/"


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

    const val AWS_ST = "st"
}