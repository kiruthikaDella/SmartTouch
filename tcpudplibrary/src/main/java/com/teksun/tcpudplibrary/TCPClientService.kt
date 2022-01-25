package com.teksun.tcpudplibrary

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.StrictMode
import android.util.Log
import com.teksun.tcpudplibrary.listener.CloseSocketListener
import com.teksun.tcpudplibrary.listener.ConnectCResultListener
import com.teksun.tcpudplibrary.listener.ReadWriteValueListener
import java.io.BufferedReader
import java.io.EOFException
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.nio.charset.Charset

@SuppressLint("StaticFieldLeak")
object TCPClientService {
    private val logTag = TCPClientService::class.java.simpleName
    private var socket: Socket? = null
    private var isEnableLog = false
    private var readWriteValueListener: ReadWriteValueListener<String>? = null
    private var connectCResultListener: ConnectCResultListener? = null
    private var packetLength: Int? = null
    private var remainingByteArray = byteArrayOf()
    private var remainingStringData: String? = null
    private var thread: Thread? = null
    /**
     * Enable / Disable print Log
     * @param boolean - true than enable else disable
     */
    fun enableLog(boolean: Boolean) {
        isEnableLog = boolean
    }

    /**
     * Set listener for communication between library module and project class
     */
    fun setReadWriteListener(readWriteValueListener: ReadWriteValueListener<String>?) {
        TCPClientService.readWriteValueListener = readWriteValueListener
    }

    /**
     * Set listener for connection
     */
    fun setConnectionListener(connectCResultListener: ConnectCResultListener?) {
        TCPClientService.connectCResultListener = connectCResultListener
    }
    /**
     * Connection
     * @param ip - server ip address
     * @param port - port number
     * @param timeOut - the timeout value to be used in milliseconds
     * @see ConnectCResultListener
     */
    @SuppressLint("MissingPermission")
    fun connectToAddress(
        context: Context,
        ip: String,
        port: Int,
        timeOut: Int? = null
    ) {
        threadPolicyCall()

        thread = Thread {
            try {
                if (socket != null) {
                    if (socket?.isConnected!!) {
                        printLog("Already connected")
                        connectCResultListener?.onSuccess("Already connected")
                        return@Thread
                    }
                }

                socket = Socket()

                val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

                if (connectivity != null) {
                    for (network in connectivity.allNetworks) {
                        val networkInfo = connectivity.getNetworkInfo(network)
                        if (networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                            if (networkInfo.isConnected) {
                                socket = network.socketFactory.createSocket()
                            }
                        }
                    }
                }


                val ipAddress = InetAddress.getByName(ip)
                printLog("IPAddress ${ipAddress.hostName}")

                val socketAddress = InetSocketAddress(ipAddress.hostName, port)
                printLog("socketAddress $socketAddress")

                if (timeOut != null) {
                    socket?.connect(socketAddress, timeOut)
                } else {
                    socket?.connect(socketAddress)
                }

                socket?.let {
                    if (it.isConnected) {
                        printLog("Connection is successful")
                        connectCResultListener?.onSuccess(Utils.concatDateAndTime("Connect"))

                        val sh = ServerHandlerNew()
                        sh.start()
                    }
                }

            } catch (e: IOException) {
                printLog("Connected failed : IOException $e")
                connectCResultListener?.onConnectFailure(Utils.concatDateAndTime("Can't connect"))
            } catch (e: SocketTimeoutException) {
                printLog("Connected failed : SocketTimeoutException $e")
                connectCResultListener?.onConnectFailure(Utils.concatDateAndTime("Can't connect"))
            }
        }
        thread?.start()
    }

    /**
     * return socket
     */
    fun getSocket(): Socket? {
        return socket
    }

    /**
     * Send value to server
     * Using socket out put stream pass value as bytearray
     * @param string - send value
     * concat string with \r\n
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun sendDefaultValue(
        string: String, readWriteValueListener: ReadWriteValueListener<String>
    ) {
        var finalStr = string
        when (SettingsService.getSendTerminateCode()) {
            Utils.SEND_CR -> {
                finalStr = string + "\r"
            }
            Utils.SEND_LF -> {
                finalStr = string + "\n"
            }
            Utils.SEND_CR_LF -> {
                finalStr = string + "\r\n"
            }
        }

        if (SettingsService.isSendHex()) finalStr = Utils.convertStringToHex(finalStr)

        try {
            socket?.let {
                val outPutStream = it.getOutputStream()
                outPutStream?.write(finalStr.toByteArray(Charset.defaultCharset()))
                printLog("outputWriteString successful")
                readWriteValueListener.onSuccess(
                    "Write String successful",
                    Utils.concatDateAndTime(finalStr)
                )
                outPutStream?.flush()
            } ?: readWriteValueListener.onFailure("Socket is null")
        } catch (e: java.lang.Exception) {
            printLog("outputWriteString $e")
            readWriteValueListener.onFailure("Write String Failed $e")
        }
    }

    //
    //region server handler class
    //

    class ServerHandlerNew() : Thread() {
        override fun run() {
            printLog("ServerHandlerNew called")

            try {
                when (SettingsService.getReadMethod()){
                    Utils.READ_CRLF -> {
                        readValuesUsingReadLines()
                    }
                    Utils.READ_PACKET_LENGTH -> {
                        readValueUsingPacketLength()
                    }
                    Utils.READ_START_END_BIT -> {
                        readValueBetweenStartAndEndBits()
                    }
                    else -> readNoneValue()
                }

            } catch (e: EOFException) {
                printLog("Exception EOF in ServerHandler $e")
                exceptionHandler()
            } catch (e: Exception) {
                printLog("Exception in ServerHandler $e")
                exceptionHandler()
            } finally {
                printLog("Finally in ServerHandler")
                exceptionHandler()
            }
        }
    }

    private fun exceptionHandler() {
        if (socket != null && !socket?.isClosed!!) {
            socket?.close()
            socket = null
            remainingByteArray = byteArrayOf()
            remainingStringData = null
            printLog("Server is disconnected")
            connectCResultListener?.onServerDisconnect(Utils.concatDateAndTime("Server is disconnected"))
        }
    }

    /**
     * Read default values
     * Set none value in setting service
     * @see SettingsService.saveReadMethodNone
     */
    private fun readNoneValue() {
        printLog("readNoneValue")
        try {
            val inputStream = socket?.getInputStream()
            var stringData: String = ""
            var data = ByteArray(1024)
            while ((inputStream?.read(data)) != -1) {
                stringData = Utils.trimByteArray(data).toString(Charset.defaultCharset())
                when (SettingsService.getDisplayMethod()){
                    Utils.DISPLAY_PLAIN_TEXT -> {
                        printLog("Read none Value plain $stringData")
                        readWriteValueListener?.onSuccess(
                            "Read none value plain",
                            Utils.concatDateAndTime(stringData)
                        )
                    }
                    Utils.DISPLAY_DECIMAL -> {
                        stringData = stringData.toByteArray(
                            Charset.defaultCharset()
                        ).let {
                            Utils.convertByteToDecimal(it)
                        }
                        printLog("Read none Value decimal $stringData")
                        readWriteValueListener?.onSuccess(
                            "Read none value",
                            Utils.concatDateAndTime(stringData)
                        )
                    }
                    Utils.DISPLAY_HEX_STRING -> {
                        stringData = Utils.convertStringToHex(stringData)
                        printLog("Read none Value hex $stringData")
                        readWriteValueListener?.onSuccess(
                            "Read none value",
                            Utils.concatDateAndTime(stringData)
                        )
                    }
                }
                data = ByteArray(1024)
            }
        } catch (e: java.lang.Exception) {
            printLog("Exception in read none value $e")
        }
    }

    /**
     * read values using read line
     * set read line in setting service
     * @see SettingsService.saveReadMethodCRLF
     */
    private fun readValuesUsingReadLines() {
        printLog("readValuesUsingReadLines")
        try {
            val inputStream = socket?.getInputStream()

            val input = BufferedReader(InputStreamReader(inputStream))
            var stringData: String? = null
            while (input.readLine().also { stringData = it } != null) {

                stringData?.let { data ->

                    when (SettingsService.getDisplayMethod()) {
                        Utils.DISPLAY_PLAIN_TEXT -> {
                            printLog("Read Values Using ReadLines plain $data")
                            readWriteValueListener?.onSuccess(
                                "Read Values Using ReadLines",
                                Utils.concatDateAndTime(data.trim())
                            )
                        }
                        Utils.DISPLAY_DECIMAL -> {
                            stringData = Utils.convertByteToDecimal(
                                data.toByteArray(Charset.defaultCharset())
                            )
                            printLog("Read Values Using ReadLines decimal $stringData")
                            readWriteValueListener?.onSuccess(
                                "Read Values Using ReadLines",
                                Utils.concatDateAndTime(stringData!!)
                            )
                        }
                        Utils.DISPLAY_HEX_STRING -> {
                            stringData = Utils.convertStringToHex(data)
                            printLog("Read Values Using ReadLines hex $stringData")
                            readWriteValueListener?.onSuccess(
                                "Read Values Using ReadLines",
                                Utils.concatDateAndTime(stringData!!)
                            )
                        }
                        else -> {
                        }
                    }
                } ?: printLog("String data null")

            }
        } catch (e: Exception) {
            printLog("Exception in Read Values Using ReadLines $e")
        }
    }

    /**
     * read value using packet length
     * set packet length method and enter packet length value
     * @see SettingsService.savePacketLength for save packetLength
     * @see SettingsService.saveReadMethodPacketLength
     */
    private fun readValueUsingPacketLength() {
        val packetLength: Int = SettingsService.getPacketLength()
        printLog("packetLength is $packetLength")

        try {
            val inputStream = socket?.getInputStream()

            var data = ByteArray(1024)
            var isFirstSet = true

            if (packetLength != 0) {
                var count: Int = 0

                // read byte array up to set packet length [count] and stored in data 
                while ((inputStream?.read(data, count, packetLength)) != -1) {
                    // Increase count for read new byte array
                    count += packetLength
                    Log.d(
                        logTag,
                        "remainingByteArray is ${remainingByteArray.toString(Charset.defaultCharset())}"
                    )

                    /**
                     * Check old remaining byte array is empty
                     * if not empty than fill all read byte array append with remaining byte array
                     * and after get data array in remainingBytearray [packetLength]
                     * else data = read all data
                     */

                    if (remainingByteArray.isNotEmpty()) {
                        remainingByteArray += Utils.trimByteArray(data)
                        isFirstSet = false

                        data = remainingByteArray.take(packetLength).toByteArray()

                        Log.d(
                            logTag,
                            "remainingByteArray is ${remainingByteArray.toString(Charset.defaultCharset())}"
                        )

                    } else {
                        data = Utils.trimByteArray(data)
                    }

                    if (data.size == packetLength) {

                        printLog("Equal Size")

                        if (!isFirstSet) {
                            isFirstSet = true
                            remainingByteArray = remainingByteArray.drop(packetLength)
                                .toByteArray()  // remove first packet length of bytes in remaining byte array
                        }

                        //Set value in interface for transfer to activity
                        setDataValueInInterface(data)

                    } else {
                        printLog("Not Equal Size")
                        if (isFirstSet) {
                            remainingByteArray = Utils.trimByteArray(data)
                            Log.d(
                                logTag,
                                "remainingByteArray is ${remainingByteArray.toString(Charset.defaultCharset())}"
                            )
                        }
                    }

                    data = ByteArray(1024)
                }
            } else {
                // If packet length set 0
                while ((inputStream?.read(data)) != -1) {
                    setDataValueInInterface(data)
                }
            }

        } catch (e: Exception) {
            printLog("Read Value Using PacketLength failed $e")
        } catch (e: ArrayIndexOutOfBoundsException) {
            printLog("Read Value Using PacketLength failed $e")
            readWriteValueListener?.onFailure("Read Value Using PacketLength failed $e")
        }
    }

    private fun setDataValueInInterface(data: ByteArray) {
        var stringResult: String? = null

        when (SettingsService.getDisplayMethod()){
            Utils.DISPLAY_PLAIN_TEXT -> {
                stringResult = (data).toString(Charset.defaultCharset())
                printLog("Read Value Using PacketLength plain $stringResult")

                readWriteValueListener?.onSuccess(
                    "Read Value Using PacketLength successful",
                    Utils.concatDateAndTime(stringResult)
                )
            }
            Utils.DISPLAY_DECIMAL -> {
                stringResult = Utils.convertByteToDecimal(data)
                printLog("Read Value Using PacketLength decimal $stringResult")
                readWriteValueListener?.onSuccess(
                    "Read Value Using PacketLength",
                    Utils.concatDateAndTime(stringResult)
                )
            }
            Utils.DISPLAY_HEX_STRING -> {
                stringResult = (data).toHexString()
                printLog("Read Value Using PacketLength hex $stringResult")
                readWriteValueListener?.onSuccess(
                    "Read Value Using PacketLength",
                    Utils.concatDateAndTime(stringResult)
                )
            }
            else -> {
            }
        }
    }

    /**
     * Read value between start and end bits value
     * Set method start bit and end bit and enter value of start bit and end bits
     * @see SettingsService.saveReadMethodStartEndBit for save methods
     * @see SettingsService.saveStartBit for save start bit value
     * @see SettingsService.saveEndBit for save end bit value
     */
    private fun readValueBetweenStartAndEndBits() {
        val startBit = SettingsService.getStartBit()
        val endBits = SettingsService.getEndBit()

        val inputStream = socket?.getInputStream()
        var stringData: String = ""
        var finalResultString: String = ""
        var data = ByteArray(1024)
        while ((inputStream?.read(data)) != -1) {

            try {
                if (!remainingStringData.isNullOrBlank()) {
                    remainingStringData += Utils.trimByteArray(data).toString(Charset.defaultCharset())

                    stringData = remainingStringData!!
                    remainingStringData = ""
                    Log.e(logTag, "remainingStringData not null blank $stringData")

                } else {
                    stringData = Utils.trimByteArray(data).toString(Charset.defaultCharset())
                    Log.e(logTag, "remainingStringData null blank $stringData")
                }

                if (stringData.contains(startBit) && stringData.contains(endBits)) {
                    var startBitIndex: Int? = null
                    var endBitIndex: Int? = null
                    var isSetStartIndex = false

                    var i = 0
                    while (i < stringData.length) {
//                        printLog("Read Value Between Start And End Bits ${stringData[i]}")
                        if (stringData[i].toString() == startBit) {
                            if (!isSetStartIndex) {
                                startBitIndex = i
                                isSetStartIndex = true
//                                i++
//                                continue
                            }
                        }

                        if (stringData[i].toString() == endBits) {
                            endBitIndex = i
                        }

                        if (startBitIndex != null) {
                            if (endBitIndex != null) {
                                if (startBitIndex < endBitIndex) {
                                    Log.d("Binajl", "s < e")

                                    finalResultString =
                                        stringData.substring(startBitIndex + 1, endBitIndex)

                                    if (finalResultString.isNotBlank()) {

                                        when (SettingsService.getDisplayMethod()) {
                                            Utils.DISPLAY_PLAIN_TEXT -> {
                                                printLog("Read Value Between Start And End Bits plain $finalResultString")
                                                readWriteValueListener?.onSuccess("Read Value Between Start And End Bits",
                                                    Utils.concatDateAndTime(finalResultString)
                                                )
                                            }
                                            Utils.DISPLAY_DECIMAL -> {
                                                finalResultString =
                                                    finalResultString.toByteArray(Charset.defaultCharset())
                                                        .let {
                                                            Utils.convertByteToDecimal(it)
                                                        }
                                                printLog("Read Value Between Start And End Bits decimal $finalResultString")
                                                readWriteValueListener?.onSuccess(
                                                    "Read Value Between Start And End Bits",
                                                    Utils.concatDateAndTime(finalResultString)
                                                )
                                            }
                                            Utils.DISPLAY_HEX_STRING -> {
                                                finalResultString =
                                                    Utils.convertStringToHex(finalResultString)
                                                printLog("Read Value Between Start And End Bits hex $finalResultString")
                                                readWriteValueListener?.onSuccess(
                                                    "Read Value Between Start And End Bits",
                                                    Utils.concatDateAndTime(finalResultString)
                                                )
                                            }
                                            else -> { }
                                        }
                                    }

                                    val subStr = stringData.substring(endBitIndex + 1, stringData.length)

                                    remainingStringData = ""

                                    if (subStr.contains(startBit)) {
                                        remainingStringData = subStr
                                        Log.e(
                                            logTag,
                                            "remainingStringData data is $remainingStringData"
                                        )
                                    }

                                    startBitIndex = null
                                    endBitIndex = null
                                    isSetStartIndex = false
                                } else {
                                    Log.d(logTag, "s > e")

                                    val subStr = stringData.substring(endBitIndex + 1, stringData.length)

                                    remainingStringData = ""

                                    if (subStr.contains(startBit)) {
                                        remainingStringData = subStr
                                        Log.e(
                                            logTag,
                                            "remainingStringData data is $remainingStringData"
                                        )
                                    }
                                    endBitIndex = null
                                }
                            } else {
                                Log.d(logTag, "End bit null")
                            }
                        } else {
                            Log.d(logTag, "Start bit null")
                        }
                        i++
                    }
                    data = ByteArray(1024)
                } else if (stringData.contains(startBit)) {
                    remainingStringData = ""
                    remainingStringData = stringData
                    Log.d(logTag, "remainingStringData data is $remainingStringData")
                }
            } catch (e: java.lang.Exception) {
                printLog("Exception in Read Value Between Start And End Bits $e")
            }
        }
    }
    //
    //endregion
    //

    /**
     * Close socket - disconnect socket
     * @param closeSocketListener - closeSocketListener get result of success or failed
     * @see closeSocketListener
     */
    fun closeSocket(closeSocketListener: CloseSocketListener) {
        try {
            if (socket != null) {
                socket!!.close()
                socket = null
                remainingByteArray = byteArrayOf()
                remainingStringData = null

                printLog("Socket closed successful")
                closeSocketListener.onSuccess(Utils.concatDateAndTime("Disconnect"))
            } else {
                printLog("Socket null")
            }
        } catch (e: IOException) {
            printLog("Socket closed failed : IOException $e")
            closeSocketListener.onFailure("Closed failed $e")
        }
    }

    /**
     * Print log if log is enable else not print
     * @param message - print message in log
     */
    private fun printLog(message: String) {
        if (isEnableLog) Log.i(logTag, message)
    }

    /**
     * Thread policy for resolve networkMainThread Exceptions
     */
    private fun threadPolicyCall() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }


}