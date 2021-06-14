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
    const val DEVICE_STATUS = "/smarttouch/$AWS_DEVICE_ID/status/" // Current Status Update - Online/Offline
    const val CONTROL_DEVICE_SWITCHES = "/smarttouch/$AWS_DEVICE_ID/control/"  //Control Device Switches
    const val GET_SWITCH_STATUS = "/smarttouch/$AWS_DEVICE_ID/swstatus/" // Responce of Get Switch status
    const val UPDATE_DEVICE_FEATURE = "/smarttouch/$AWS_DEVICE_ID/features-settings/" // Update Device Feature Settings


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

    //Device
    const val AWS_ST = "st" //Status
    const val AWS_DT = "DT" //Device Type
    const val AWS_SW = "SW" //Switch
    const val AWS_U1 = "U1" //USB A
    const val AWS_U2 = "U2" //USB C
    const val AWS_D = "D"   //DMR

    //Features
    const val AWS_SM = "sm"   //Sleep Mode
    const val AWS_NM = "nm"   //Night Mode
    const val AWS_TI = "ti"   //Time Display
    const val AWS_TF = "tf"   //Time Format
    const val AWS_OM = "om"   //Outdoor Mode
    const val AWS_WR = "wr"   //weather Report Display
    const val AWS_RT = "rt"   //Room Temperature Display
    const val AWS_TU = "tu"   //Temperature Unit
    const val AWS_BM = "bm"   //Brightness Model
    const val AWS_BV = "bv"   //Brightness value

}