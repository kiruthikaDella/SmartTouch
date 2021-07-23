package com.teksun.tcpudplibrary

import android.annotation.SuppressLint
import android.content.Context
import com.appizona.yehiahd.fastsave.FastSave
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StaticFieldLeak")
object SettingsService {
    private var mContext: Context? = null

    /**
     * Initialize with context in Application class
     * For manage FastSave(sharedpreference) library initialization
     * @param context
     */
    fun init(context: Context?) {
        mContext = context

        FastSave.init(context?.applicationContext)

        if (!FastSave.getInstance().isKeyExists(Utils.SEND_METHOD)) {
            FastSave.getInstance().saveInt(Utils.SEND_METHOD, 0)
        }
        if (!FastSave.getInstance().isKeyExists(Utils.READ_METHOD)) {
            FastSave.getInstance().saveInt(Utils.READ_METHOD, 0)
        }
        if (!FastSave.getInstance().isKeyExists(Utils.DISPLAY_METHOD)) {
            FastSave.getInstance().saveInt(Utils.DISPLAY_METHOD, 0)
        }
        if (!FastSave.getInstance().isKeyExists(Utils.SAVE_SEND_BACKGROUND_COLOR)) {
            FastSave.getInstance().saveInt(Utils.SAVE_SEND_BACKGROUND_COLOR, android.R.color.transparent)
        }
        if (!FastSave.getInstance().isKeyExists(Utils.SAVE_RECEIVE_BACKGROUND_COLOR)) {
            FastSave.getInstance().saveInt(Utils.SAVE_RECEIVE_BACKGROUND_COLOR, android.R.color.transparent)
        }

        if (!FastSave.getInstance().isKeyExists(Utils.PACKET_LENGTH_VALUE)) {
            FastSave.getInstance().saveInt(Utils.PACKET_LENGTH_VALUE, 5)
        }

        if (!FastSave.getInstance().isKeyExists(Utils.START_BIT)) {
            FastSave.getInstance().saveString(Utils.START_BIT, "*")
        }

        if (!FastSave.getInstance().isKeyExists(Utils.END_BIT)) {
            FastSave.getInstance().saveString(Utils.END_BIT, "#")
        }
    }

    //
    //region Send Terminate code
    //
    fun saveSendTerminateNone() {
        if (mContext == null) {
            validateInitialization()
            return
        }
        saveTerminate(Utils.SEND_NONE)
    }

    fun saveSendTerminateCR() {
        if (mContext == null) {
            validateInitialization()
            return
        }
        saveTerminate(Utils.SEND_CR)
    }

    fun saveSendTerminateLF() {
        if (mContext == null) {
            validateInitialization()
            return
        }
        saveTerminate(Utils.SEND_LF)
    }

    fun saveSendTerminateCRLF() {
        if (mContext == null) {
            validateInitialization()
            return
        }
        saveTerminate(Utils.SEND_CR_LF)
    }

    private fun saveTerminate(sendValue: Int) {
        FastSave.getInstance().saveInt(Utils.SEND_METHOD, sendValue)

    }

    fun getSendTerminateCode(): Int {
       return FastSave.getInstance().getInt(Utils.SEND_METHOD, 0)
    }
    //
    //endregion
    //

    //
    //region Send hex
    //
    fun saveSendHex(isSend: Boolean) {
        if (mContext == null) {
            validateInitialization()
            return
        }
        FastSave.getInstance().saveBoolean(Utils.SEND_HEX, isSend)
    }

    fun isSendHex(): Boolean {
        if (mContext == null) {
            validateInitialization()
        }
        return FastSave.getInstance().getBoolean(Utils.SEND_HEX, false)
    }

    //
    //endregion
    //

    //
    //region Read method
    //
    fun saveReadMethodNone() {
        if (mContext == null) {
            validateInitialization()
            return
        }
        saveReadMethod(Utils.READ_NONE)
    }

    fun saveReadMethodCRLF() {
        if (mContext == null) {
            validateInitialization()
            return
        }
        saveReadMethod(Utils.READ_CRLF)
    }

    fun saveReadMethodPacketLength() {
        if (mContext == null) {
            validateInitialization()
            return
        }
        saveReadMethod(Utils.READ_PACKET_LENGTH)
    }

    fun saveReadMethodStartEndBit() {
        if (mContext == null) {
            validateInitialization()
            return
        }
        saveReadMethod(Utils.READ_START_END_BIT)
    }

    private fun saveReadMethod(readValue: Int) {
        FastSave.getInstance().saveInt(Utils.READ_METHOD, readValue)
    }

    fun getReadMethod(): Int {
        return FastSave.getInstance().getInt(Utils.READ_METHOD, 0)
    }
    fun savePacketLength(packetLength: Int) {
        if (mContext == null) {
            validateInitialization()
            return
        }
        FastSave.getInstance().saveInt(Utils.PACKET_LENGTH_VALUE, packetLength)
    }

    fun getPacketLength(): Int {
        if (mContext == null) {
            validateInitialization()
        }
        return FastSave.getInstance().getInt(Utils.PACKET_LENGTH_VALUE, 5)
    }

    fun saveStartBit(startBit: String) {
        if (mContext == null) {
            validateInitialization()
            return
        }
        FastSave.getInstance().saveString(Utils.START_BIT, startBit)
    }

    fun getStartBit(): String {
        if (mContext == null) {
            validateInitialization()
        }
        return FastSave.getInstance().getString(Utils.START_BIT, "*")
    }

    fun saveEndBit(endBit: String) {
        if (mContext == null) {
            validateInitialization()
            return
        }
        FastSave.getInstance().saveString(Utils.END_BIT, endBit)
    }

    fun getEndBit(): String {
        if (mContext == null) {
            validateInitialization()
        }
        return FastSave.getInstance().getString(Utils.END_BIT, "#")
    }

    //
    //endregion
    //

    //
    //region Display method
    //
    fun saveDisplayPlainText() {
        if (mContext == null) {
            validateInitialization()
            return
        }
        FastSave.getInstance().saveInt(Utils.DISPLAY_METHOD, Utils.DISPLAY_PLAIN_TEXT)
    }

    fun saveDisplayHexString() {
        if (mContext == null) {
            validateInitialization()
            return
        }
        FastSave.getInstance().saveInt(Utils.DISPLAY_METHOD, Utils.DISPLAY_HEX_STRING)

    }

    fun saveDisplayDecimal() {
        if (mContext == null) {
            validateInitialization()
            return
        }
        FastSave.getInstance().saveInt(Utils.DISPLAY_METHOD, Utils.DISPLAY_DECIMAL)

    }

    fun getDisplayMethod(): Int {
        return FastSave.getInstance().getInt(Utils.DISPLAY_METHOD, 0)
    }

    fun saveDisplayDate(isTrue: Boolean) {
        if (mContext == null) {
            validateInitialization()
            return
        }
         FastSave.getInstance().saveBoolean(Utils.DISPLAY_DATE, isTrue)
    }

    fun isDisplayDate(): Boolean {
        if (mContext == null)
            validateInitialization()

        return FastSave.getInstance().getBoolean(Utils.DISPLAY_DATE, false)
    }

    fun saveDisplayTime(isTrue: Boolean) {
        if (mContext == null) {
            validateInitialization()
            return
        }
       FastSave.getInstance().saveBoolean(Utils.DISPLAY_TIME, isTrue)
    }

    fun isDisplayTime(): Boolean {
        if (mContext == null)
            validateInitialization()

        return FastSave.getInstance().getBoolean(Utils.DISPLAY_TIME, false)
    }

    fun getCurrentDate(): String {
        return "[" + SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()) + "]"
    }

    fun getCurrentTime(): String {
        return "[" + SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault()).format(Date()) + "]"
    }

    //
    //endregion
    //

    //
    //region Background color
    //
    fun saveSendBackgroundColor(color: Int) {
        /*var savedColor : Int ?= null
        savedColor = when(color) {
            "Green" -> {
                android.R.color.holo_green_light
            }
            "Orange" -> {
                android.R.color.holo_orange_light
            }
            "Purple" -> {
                android.R.color.holo_purple
            } else -> android.R.color.transparent
        }*/
        FastSave.getInstance().saveInt(Utils.SAVE_SEND_BACKGROUND_COLOR, color)
    }

    fun saveReceivedBackgroundColor(color: Int) {
        /*var savedColor : Int ?= null
        savedColor = when(color) {
            "Blue" -> {
                android.R.color.holo_blue_light
            }
            "Gray" -> {
                android.R.color.darker_gray
            }
            "Red" -> {
                android.R.color.holo_red_light
            } else -> android.R.color.transparent
        }*/
        FastSave.getInstance().saveInt(Utils.SAVE_RECEIVE_BACKGROUND_COLOR, color)
    }

    fun getSendBackgroundColor(): Int {
        return FastSave.getInstance().getInt(Utils.SAVE_SEND_BACKGROUND_COLOR, android.R.color.transparent)
    }

    fun getReceivedBackgroundColor(): Int {
        return FastSave.getInstance().getInt(Utils.SAVE_RECEIVE_BACKGROUND_COLOR, android.R.color.transparent)
    }

    fun saveTCPClientIPAndPort(ipaddress: String, port: String) {
        FastSave.getInstance().saveString(Utils.TCP_CLIENT_IP_ADDRESS, ipaddress)
        FastSave.getInstance().saveString(Utils.TCP_CLIENT_PORT, port)
    }

    fun getTCPClientIP() : String{
        return FastSave.getInstance().getString(Utils.TCP_CLIENT_IP_ADDRESS, "192.168.4.90")
    }

    fun getTCPClientPort(): String {
        return FastSave.getInstance().getString(Utils.TCP_CLIENT_PORT, "8881")
    }

    fun saveTCPServerPort(port: String) {
        FastSave.getInstance().saveString(Utils.TCP_SERVER_PORT, port)
    }

    fun getTCPServerPort(): String {
        return FastSave.getInstance().getString(Utils.TCP_SERVER_PORT, "8881")
    }

    fun saveUDPIPAndPort(ipaddress: String, targetPort: String, localPort: String) {
        FastSave.getInstance().saveString(Utils.UDP_IP_ADDRESS, ipaddress)
        FastSave.getInstance().saveString(Utils.UDP_TARGET_PORT, targetPort)
        FastSave.getInstance().saveString(Utils.UDP_LOCAL_PORT, localPort)
    }

    fun getUdpIP(): String {
        return FastSave.getInstance().getString(Utils.UDP_IP_ADDRESS, "192.168.4.64")
    }

    fun getUdpLocalPort(): String {
        return FastSave.getInstance().getString(Utils.UDP_LOCAL_PORT, "8881")
    }

    fun getUdpTargetPort(): String {
        return FastSave.getInstance().getString(Utils.UDP_TARGET_PORT, "8880")
    }
    //
    //endregion
    //

    /**
     * Validate context is initialize
     */
    private fun validateInitialization() {
        if (mContext == null)
            throw RuntimeException("Settings service must be initialize inside your application class by calling ${SettingsService::class.java.simpleName}.init(this)")
    }

}