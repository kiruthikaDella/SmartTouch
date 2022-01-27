package com.teksun.tcpudplibrary

import android.annotation.SuppressLint
import android.os.StrictMode
import android.util.Log
import com.appizona.yehiahd.fastsave.FastSave
import com.teksun.tcpudplibrary.listener.CloseSocketListener
import com.teksun.tcpudplibrary.listener.ConnectResultListener
import com.teksun.tcpudplibrary.listener.ReadWriteValueListener
import java.io.BufferedReader
import java.io.EOFException
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.nio.charset.Charset


@SuppressLint("StaticFieldLeak")
object TCPServerService {
    private val logTag = TCPServerService::class.java.simpleName
    private var serverSocket: ServerSocket? = null
    private var socket: Socket? = null
    private var isEnableLog = false
    private var connectResultListener: ConnectResultListener<Socket>? = null
    private var readWriteValueListener: ReadWriteValueListener<String>? = null
    private var packetLength: Int? = null
    private var connectedMap: HashMap<String, Socket>? = HashMap()
    private var clientId = 0


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
        TCPServerService.readWriteValueListener = readWriteValueListener
    }

    /**
     * Initialize server socket
     * Create Socket
     * listen - establish connection
     * @param port - port number
     * @param timeOut - the specified timeout in millisecond
     * @see ConnectResultListener
     */
    fun connectWithPort(
        port: Int,
        timeOut: Int? = null) {

        threadPolicyCall()

        val thread = Thread {
            try {
                serverSocket = ServerSocket(port)
                printLog("socketAddress $serverSocket")

                if (timeOut != null) {
                    serverSocket?.soTimeout = timeOut
                }

                while (!serverSocket?.isClosed!!) {
                    socket = serverSocket?.accept()
                    connectedMap?.put("Client $clientId", socket!!)

                    printLog("Just Connected with Client $clientId and Port is ${socket?.port} ")
                    connectResultListener?.onSuccess(
                        Utils.concatDateAndTime("Connected Client $clientId"),
                        connectedMap
                    )
                    clientId++

                    socket?.let {
                        val ct1 = ClientHandlerNew(it)
                        ct1.start()
                    }
                }
            } catch (e: SocketException) {
                printLog("connection failed Socket closed $e")
            } catch (e: IOException) {
                printLog("Connect failed $e")
                connectResultListener?.onFailure(Utils.concatDateAndTime("Can't connect"))
            } catch (e: Exception) {
                printLog("Exception $e")
            }
        }
        thread.start()
    }

    /**
     * Send value to all connected Client
     * Using socket out put stream pass value as bytearray
     * @param string - send value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun sendDefaultValueAll(
        string: String,
        readWriteValueListener: ReadWriteValueListener<String>
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

        connectedMap?.let {
            for (key in it.keys) {
                socket = it[key]
                try {
                    val outPutStream = socket?.getOutputStream()

                    outPutStream?.write(finalStr.toByteArray(Charset.defaultCharset()))
                    outPutStream?.flush()
                } catch (e: java.lang.Exception) {
                    printLog("outputWriteString $e")
                    readWriteValueListener.onFailure("Write String Failed $e")
                }
            }
            printLog("outputWriteString successful")
            readWriteValueListener.onSuccess(
                "Write String successful",
                Utils.concatDateAndTime(finalStr)
            )
        } ?: printLog("No any connected client")
    }

    /**
     * Send value to single connected Client
     * Using socket out put stream pass value as bytearray
     * @param name - single client static name, get name using connected map
     * @param string - send value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun sendDefaultValueIndividual(
        name: String,
        string: String,
        readWriteValueListener: ReadWriteValueListener<String>
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

        connectedMap?.let { it ->
            val mKey = it.filterKeys { it == name }
            if (mKey.containsKey(name)) {
                socket = it[name]
            }
        } ?: printLog("No any connected client")

        try {
            val outPutStream = socket?.getOutputStream()
            outPutStream?.write(finalStr.toByteArray(Charset.defaultCharset()))
            printLog("outputWriteString successful")
            readWriteValueListener.onSuccess(
                "Write String successful",
                Utils.concatDateAndTime(finalStr)
            )
            outPutStream?.flush()
        } catch (e: java.lang.Exception) {
            printLog("outputWriteString $e")
            readWriteValueListener.onFailure("Write String Failed $e")
        }
    }

    //
    //region Client handler new and method
    //
    class ClientHandlerNew(
        val socket: Socket) : Thread() {
        override fun run() {
            printLog("ClientHandlerNew called")

            try {
                when (SettingsService.getReadMethod()){
                    Utils.READ_CRLF -> {
                        readValuesUsingReadLines(socket)
                    }
                    Utils.READ_PACKET_LENGTH -> {
                        readValueUsingPacketLength(socket)
                    }
                    Utils.READ_START_END_BIT -> {
                        readValueBetweenStartAndEndBitsNew(socket)
                    }
                    else -> readNoneValue(socket)
                }
            } catch (e: EOFException) {
                printLog("Exception EOF in ClientHandler $e")
                exceptionHandle(socket)
            } catch (e: Exception) {
                printLog("Exception in ClientHandler $e")
                exceptionHandle(socket)
            } finally {
                printLog("Finally in ClientHandler")
                exceptionHandle(socket)
            }
        }
    }

    private fun exceptionHandle(socket: Socket) {
        if (!socket.isClosed) {
            connectedMap?.let { it ->

                val mKey = it.filterValues { it == socket }
                Log.e(logTag, "key $mKey")

                if (mKey.containsValue(socket)) {
                    it.values.remove(socket)

                    Log.e(logTag, "hh $it")

                    connectResultListener?.onSuccess(
                        (Utils.concatDateAndTime("${mKey.keys} is disconnected")),
                        it
                    )
                    FastSave.getInstance().deleteValue(Utils.REMAINING_BYTE_ARRAY_STRING + mKey.filterValues { it == socket }.keys)
                    FastSave.getInstance().deleteValue(Utils.REMAINING_STRING_DATA + mKey.filterValues { it == socket }.keys)

                } else {
                    connectResultListener?.onFailure("Disconnect failed")
                }

                if (connectedMap.isNullOrEmpty()) {
                    clientId = 0
//                    connectResultListener.onSuccess("")

//                    remainingStringData = null
                }
            } ?: printLog("No any connected client")
        }
    }

    /**
     * Read default values
     * Set none value in setting service
     * @see SettingsService.saveReadMethodNone
     */
    private fun readNoneValue(socket: Socket) {
        printLog("readNoneValue")
        try {
            val inputStream = socket.getInputStream()
            var stringData: String = ""
            var data = ByteArray(1024)
            while ((inputStream?.read(data)) != -1) {
                stringData = Utils.trimByteArray(data).toString(Charset.defaultCharset())
                when (SettingsService.getDisplayMethod()) {
                    Utils.DISPLAY_PLAIN_TEXT -> {
                        printLog("Read none Value plain $stringData")
                        readWriteValueListener?.onSuccess(
                            "Read none Value plain",
                            Utils.concatDateAndTime(stringData)
                        )
                    }
                    Utils.DISPLAY_DECIMAL -> {
                        stringData = stringData.toByteArray(Charset.defaultCharset())
                            .let { Utils.convertByteToDecimal(it) }

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
            printLog("Exception in read none value")
        }

    }

    /**
     * read values using read line
     * set read line in setting service
     * @see SettingsService.saveReadMethodCRLF
     */
    private fun readValuesUsingReadLines(socket: Socket) {
        printLog("readValuesUsingReadLines")
        try {
            val inputStream = socket.getInputStream()

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
                            stringData =
                                Utils.convertByteToDecimal(data.toByteArray(Charset.defaultCharset()))
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

    private fun readValueUsingPacketLength(socket: Socket) {
        val packetLength: Int = SettingsService.getPacketLength()
        printLog("packetLength is $packetLength")

        try {
            val inputStream = socket.getInputStream()

            var data = ByteArray(1024)
            var remainingByteArray = byteArrayOf()
            var isFirstSet = true

            if (packetLength != 0) {
                var count = 0

                // read byte array up to set packet length [count] and stored in data
                while ((inputStream?.read(data, count, packetLength)) != -1) {
                    // Increase count for read new byte array
                    count += packetLength

                    val isRemainingExists = FastSave.getInstance().isKeyExists(Utils.REMAINING_BYTE_ARRAY_STRING + connectedMap?.filterValues { it == socket }?.keys)
                    Log.e(
                        logTag,
                        "Key exists is ${FastSave.getInstance().isKeyExists(Utils.REMAINING_BYTE_ARRAY_STRING + connectedMap?.filterValues { it == socket }?.keys)}"
                    )
                    /*Log.e(
                        logTag,
                        "remainingByteArray is ${remainingByteArray.toString(Charset.defaultCharset())}"
                    )*/

                    if (isRemainingExists) {
                        remainingByteArray = FastSave.getInstance().getString(Utils.REMAINING_BYTE_ARRAY_STRING + connectedMap?.filterValues { it == socket }?.keys, "").toPreservedByteArray
                    }
                    remainingByteArray = Utils.trimByteArray(remainingByteArray)
                    /**
                     * Check old remaining byte array is empty
                     * if not empty than fill all read byte array append with remaining byte array
                     * and after get data array in remainingBytearray [packetLength]
                     * else data = read all data
                     */

                    if (remainingByteArray.isNotEmpty()) {

                        remainingByteArray += Utils.trimByteArray(data)
                        FastSave.getInstance().saveString(Utils.REMAINING_BYTE_ARRAY_STRING + connectedMap?.filterValues { it == socket }?.keys, remainingByteArray.toPreservedString)

                        isFirstSet = false

                        data = remainingByteArray.take(packetLength).toByteArray()

                        Log.e(
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
                            remainingByteArray = remainingByteArray.drop(packetLength).toByteArray()  // remove first packet length of bytes in remaining byte array
                            FastSave.getInstance().saveString(Utils.REMAINING_BYTE_ARRAY_STRING + connectedMap?.filterValues { it == socket }?.keys, remainingByteArray.toPreservedString)
                        }

                        //Set value in interface for transfer to activity
                        setDataValueInInterface(data)

                    } else {
                        printLog("Not Equal Size")
                        if (isFirstSet) {
                            remainingByteArray = Utils.trimByteArray(data)
                            Log.e(
                                logTag,
                                "remainingByteArray is ${remainingByteArray.toString(Charset.defaultCharset())}"
                            )
                            FastSave.getInstance().saveString(Utils.REMAINING_BYTE_ARRAY_STRING + connectedMap?.filterValues { it == socket }?.keys, remainingByteArray.toPreservedString)
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

        when (SettingsService.getDisplayMethod()) {
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

    private fun readValueBetweenStartAndEndBitsNew(socket: Socket) {
        val startBit = SettingsService.getStartBit()
        val endBits = SettingsService.getEndBit()

        val inputStream = socket.getInputStream()
        var remainingStringData: String? = null
        var stringData = ""
        var finalResultString = ""
        var data = ByteArray(1024)

        while ((inputStream?.read(data)) != -1) {
            if (FastSave.getInstance().isKeyExists(Utils.REMAINING_STRING_DATA + connectedMap?.filterValues { it == socket }?.keys)) remainingStringData = FastSave.getInstance().getString(Utils.REMAINING_STRING_DATA + connectedMap?.filterValues { it == socket }?.keys, "")
            try {
                if (!remainingStringData.isNullOrBlank()) {
                    remainingStringData += Utils.trimByteArray(data).toString(Charset.defaultCharset())
                    FastSave.getInstance().saveString(Utils.REMAINING_STRING_DATA + connectedMap?.filterValues { it == socket }?.keys, remainingStringData)

                    stringData = remainingStringData
                    remainingStringData = ""
                    Log.e(logTag, "remaining StringData not null blank $stringData")

                } else {
                    stringData = Utils.trimByteArray(data).toString(Charset.defaultCharset())
                    Log.e(logTag, "remaining StringData null blank $stringData")
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
//                                    printLog("s < e")

                                    finalResultString =
                                        stringData.substring(startBitIndex + 1, endBitIndex)

                                    if (finalResultString.isNotBlank()) {

                                        when (SettingsService.getDisplayMethod()) {
                                            Utils.DISPLAY_PLAIN_TEXT -> {
                                                printLog("Read Value Between Start And End Bits plain $finalResultString")
                                                readWriteValueListener?.onSuccess(
                                                    "Read Value Between Start And End Bits",
                                                    Utils.concatDateAndTime(finalResultString)
                                                )
                                            }
                                            Utils.DISPLAY_DECIMAL -> {
                                                finalResultString = finalResultString.toByteArray(
                                                    Charset.defaultCharset()
                                                ).let {
                                                    Utils.convertByteToDecimal(
                                                        it
                                                    )
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
                                        }
                                    }

                                    val subStr =
                                        stringData.substring(endBitIndex + 1, stringData.length)

                                    remainingStringData = ""
                                    FastSave.getInstance().saveString(Utils.REMAINING_STRING_DATA + connectedMap?.filterValues { it == socket }?.keys, remainingStringData)

                                    if (subStr.contains(startBit)) {
                                        remainingStringData = subStr
                                        FastSave.getInstance().saveString(Utils.REMAINING_STRING_DATA + connectedMap?.filterValues { it == socket }?.keys, remainingStringData)
                                        Log.e(
                                            logTag,
                                            "remainingStringData data is $remainingStringData"
                                        )
                                    }

                                    startBitIndex = null
                                    endBitIndex = null
                                    isSetStartIndex = false
                                } else {
                                    printLog("s > e endIndex $endBitIndex and length ${stringData.length}")

                                    val subStr =
                                        stringData.substring(endBitIndex + 1, stringData.length)

                                    remainingStringData = ""
                                    FastSave.getInstance().saveString(Utils.REMAINING_STRING_DATA + connectedMap?.filterValues { it == socket }?.keys, remainingStringData)

                                    if (subStr.contains(startBit)) {
                                        remainingStringData = subStr
                                        FastSave.getInstance().saveString(Utils.REMAINING_STRING_DATA + connectedMap?.filterValues { it == socket }?.keys, remainingStringData)
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
                    remainingStringData = stringData
                    FastSave.getInstance().saveString(Utils.REMAINING_STRING_DATA + connectedMap?.filterValues { it == socket }?.keys, remainingStringData)
                    Log.e(logTag, "remainingStringData data is $remainingStringData")
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
     * Close socket
     * @param closeSocketListener - closeSocketListener get result of success or failed
     * @see closeSocketListener
     */
    fun closeSocket(closeSocketListener: CloseSocketListener) {
        try {
            var isSocketClosed = false
            if (serverSocket != null) {
                serverSocket?.close()
                serverSocket = null
                printLog("Server socket closed successful")
                isSocketClosed = true
            } else {
                printLog("Server Socket null")
            }

            connectedMap?.let { map ->
                for (key in map.keys) {
                    socket = map[key]
                    if (socket != null) {
                        socket?.close()
                        socket = null

                        isSocketClosed = true

                        FastSave.getInstance().deleteValue(Utils.REMAINING_BYTE_ARRAY_STRING + "[$key]")
                        FastSave.getInstance().deleteValue(Utils.REMAINING_STRING_DATA + "[$key]")
                    } else {
                        printLog("Socket already closed")
                        closeSocketListener.onFailure(Utils.concatDateAndTime("Socket already closed"))
                    }
                }
                map.clear()
            }

            if (isSocketClosed) {
                printLog("socket closed successful")
                closeSocketListener.onSuccess(Utils.concatDateAndTime("Disconnect"))
            }

            clientId = 0
        } catch (e: IOException) {
            printLog("Socket closed failed : IOException $e")
            closeSocketListener.onFailure(Utils.concatDateAndTime("Can't Disconnect"))
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


