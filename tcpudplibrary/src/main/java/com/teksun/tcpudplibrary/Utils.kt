package com.teksun.tcpudplibrary

import org.json.JSONObject
import java.nio.charset.Charset

object Utils {

    /**
     * Constant key for shared preference
     */
    const val SEND_NONE = 0
    const val SEND_CR = 1
    const val SEND_LF = 2
    const val SEND_CR_LF = 3
    const val SEND_HEX = "SEND_HEX"
    const val SEND_METHOD = "SEND_METHOD"   //0 - None, 1 - CR, 2 - LF, 3- CRLF

    const val READ_NONE = 0
    const val READ_CRLF = 1
    const val READ_PACKET_LENGTH = 2
    const val READ_START_END_BIT = 3
    const val READ_METHOD = "READ_METHOD"     //0 - None, 1 - CRLF, 2 - Packet Length, 3 - Start and end bit

    const val PACKET_LENGTH_VALUE = "PACKET_LENGTH_VALUE"
    const val START_BIT = "START_BIT"
    const val END_BIT = "END_BIT"

    const val DISPLAY_PLAIN_TEXT = 0
    const val DISPLAY_HEX_STRING = 1
    const val DISPLAY_DECIMAL = 2
    const val DISPLAY_METHOD = "DISPLAY_METHOD"   //0 - Plain text, 1 - hex string, 2 - decimal

    const val DISPLAY_DATE = "DISPLAY_DATE"
    const val DISPLAY_TIME = "DISPLAY_TIME"

    const val SAVE_SEND_BACKGROUND_COLOR = "SAVE_SEND_BACKGROUND_COLOR"
    const val SAVE_RECEIVE_BACKGROUND_COLOR = "SAVE_RECEIVE_BACKGROUND_COLOR"

    const val REMAINING_BYTE_ARRAY_STRING = "REMAINING_BYTE_ARRAY_STRING"
    const val REMAINING_STRING_DATA = "REMAINING_STRING_DATA"

    const val TCP_SERVER_PORT = "TCP_SERVER_PORT"
    const val TCP_CLIENT_IP_ADDRESS = "TCP_CLIENT_IP_ADDRESS"
    const val TCP_CLIENT_PORT = "TCP_CLIENT_PORT"
    const val UDP_IP_ADDRESS = "UDP_IP_ADDRESS"
    const val UDP_TARGET_PORT = "UDP_TARGET_PORT"
    const val UDP_LOCAL_PORT = "UDP_LOCAL_PORT"


    /**
     * trim byte array
     * remove space
     * @return byte array
     */
    fun trimByteArray(byteArray: ByteArray) : ByteArray {
        return String(byteArray).replace("\u0000".toRegex(), "").toByteArray()
    }

    fun convertByteArrayToPlainText(byteArray: ByteArray): String {
        return byteArray.toString(Charset.defaultCharset())
    }

    /**
     * cont byte to decimal
     * @return string
     */
    fun convertByteToDecimal(data: ByteArray): String {
        return trimByteArray(data).joinToString(separator = " ") { it.toInt().toString() }
    }

    /**
     * Convert string to hex
     * @return string in upper case
     */
    fun convertStringToHex(str: String): String {
        val getBytesFromString: ByteArray = str.toByteArray(Charset.defaultCharset())
        return getBytesFromString.joinToString(separator = " ") { String.format("%02X", it) }.uppercase()
    }

    fun remove(arr: ByteArray, index: Int): ByteArray {
        return if (index < 0 || index >= arr.size) {
            arr
        } else (arr.indices)
            .filter { i: Int -> i != index }
            .map { i: Int -> arr[i] }
            .toByteArray()
    }

    /**
     * Concat date and time if it's selected
     * @see SettingsService.saveDisplayDate set true for concat date
     * @see SettingsService.saveDisplayTime set true for concat time
     * @return string
     */
    fun concatDateAndTime(message: String): String {
        val jsonObject = JSONObject()
        jsonObject.put("data", message)

        if (SettingsService.isDisplayDate()) {
            jsonObject.put("date", SettingsService.getCurrentDate())
        }

        if (SettingsService.isDisplayTime()) {
            jsonObject.put("time", SettingsService.getCurrentTime())
        }
        return jsonObject.toString()
    }
}

val String.toPreservedByteArray: ByteArray
    get() {
        return this.toByteArray(Charsets.ISO_8859_1)
    }

val ByteArray.toPreservedString: String
    get() {
        return String(this, Charsets.ISO_8859_1)
    }

/**
 * convert byte array to hex string
 * @return string
 */
fun ByteArray.toHexString(): String {
    return this.joinToString(separator = " ") { String.format("%02x", it) }.uppercase()
}