package com.voinismartiot.voni.mqtt

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

    //MQTT TOPIC
    const val AWS_DEVICE_ID = "{deviceid}" //Device Serial number
    const val DEVICE_STATUS = "/smarttouch/$AWS_DEVICE_ID/status/" // Current Status Update - Online/Offline
    const val CONTROL_DEVICE_SWITCHES = "/smarttouch/$AWS_DEVICE_ID/control/"  //Control Device Switches
    const val GET_SWITCH_STATUS = "/smarttouch/$AWS_DEVICE_ID/swstatus/" // Responce of Get Switch status
    const val UPDATE_DEVICE_FEATURE = "/smarttouch/$AWS_DEVICE_ID/features-settings/" // Update Device Feature Settings
    const val DEVICE_FEATURE_ACK = "/smarttouch/$AWS_DEVICE_ID/features-settings-ack/" // Device Feature Settings ACK
    const val UPDATE_DEVICE_CUSTOMIZATION = "/smarttouch/$AWS_DEVICE_ID/customization/" // Update Device Customization Settings
    const val DEVICE_CUSTOMIZATION_ACK = "/smarttouch/$AWS_DEVICE_ID/customization-ack/" // Device Customization Settings ACK
    const val RESTART_DEVICE = "/smarttouch/$AWS_DEVICE_ID/restart/"        //Restart device
    const val RESTORE_FACTORY_SETTINGS = "/smarttouch/$AWS_DEVICE_ID/restore/"  //Restore factory settings

    //MQTT Parameter
    const val AWS_SWITCH_1 = "SW01"
    const val AWS_SWITCH_2 = "SW02"
    const val AWS_SWITCH_3 = "SW03"
    const val AWS_SWITCH_4 = "SW04"
    const val AWS_SWITCH_5 = "SW05"
    const val AWS_SWITCH_6 = "SW06"
    const val AWS_SWITCH_7 = "SW07"
    const val AWS_SWITCH_8 = "SW08"
    const val AWS_DMR = "DMR"
    const val AWS_USB_PORT_A = "USB1"
    const val AWS_USB_PORT_C = "USB2"
    const val AWS_NAME = "name"

    //Device
    const val AWS_STATUS = "st" //Status
    const val AWS_DEVICE_TYPE = "DT" //Device Type
    const val AWS_SWITCH = "SW" //Switch
    const val AWS_USB_A = "U1" //USB A
    const val AWS_USB_C = "U2" //USB C
    const val AWS_DIMMER = "D"   //DMR

    //Features
    const val AWS_SLEEP_MODE = "sm"   //Sleep Mode
    const val AWS_NIGHT_MODE = "nm"   //Night Mode
    const val AWS_TIME_DISPLAY = "ti"   //Time Display
    const val AWS_TIME_FORMAT = "tf"   //Time Format
    const val AWS_OUTDOOR_MODE = "om"   //Outdoor Mode
    const val AWS_WEATHER_REPORT_DISPLAY = "wr"   //weather Report Display
    const val AWS_ROOM_TEMPERATURE_DISPLAY = "rt"   //Room Temperature Display
    const val AWS_TEMPERATURE_UNIT = "tu"   //Temperature Unit
    const val AWS_BRIGHTNESS_MODE = "bm"   //Brightness Model
    const val AWS_BRIGHTNESS_VALUE = "bv"   //Brightness value

    //Customization
    const val AWS_UPLOAD_IMAGE = "img"   //Image URL
    const val AWS_SCREEN_LAYOUT_TYPE = "slt"   //Screen layout type
    const val AWS_SCREEN_LAYOUT = "sl"   //Screen layout
    const val AWS_SWITCH_NAME = "swn"   //Switch Name
    const val AWS_SWITCH_ICON_SIZE = "swis"   //Switch Icon size
    const val AWS_TEXT_STYLE = "tsy"   //Text style
    const val AWS_TEXT_COLOR = "tc"   //Text Color
    const val AWS_TEXT_SIZE = "tsz"   //Text Size
    const val AWS_CUSTOMIZATION_LOCK = "cl"   //Customization lock
    const val AWS_SWITCH_ICONS = "swicons"   //Customization lock

    //Restart
    const val AWS_RESTART_DEVICE = "rd"

    //Factory Reset Settings
    const val AWS_FACTORY_RESET = "rsf"

}