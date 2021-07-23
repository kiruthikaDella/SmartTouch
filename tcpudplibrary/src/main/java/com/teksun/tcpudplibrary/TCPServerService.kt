package com.teksun.tcpudplibrary

import android.annotation.SuppressLint
import android.content.Context
import android.os.StrictMode
import android.util.Log
import com.appizona.yehiahd.fastsave.FastSave
import com.teksun.tcpudplibrary.listener.CloseSocketListener
import com.teksun.tcpudplibrary.listener.ConnectResultListener
import com.teksun.tcpudplibrary.listener.ReadWriteValueListener
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.HashMap


@SuppressLint("StaticFieldLeak")
object TCPServerService {
    private val logTag = TCPServerService::class.java.simpleName
    private var serverSocket: ServerSocket? = null
    private var socket: Socket? = null
    private var isEnableLog = false
    private var readWriteValueListener: ReadWriteValueListener<String>? = null
    private var packetLength: Int? = null
    private var fileName: String? = null
    private var fileSize: Int? = null
    private var connectedMap: HashMap<String, Socket>? = HashMap()
    private var clientId = 0

//    private var remainingByteArray = byteArrayOf()
//    private var remainingStringData: String? = null

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
    fun setReadWriteListener(readWriteValueListener: ReadWriteValueListener<String>) {
        TCPServerService.readWriteValueListener = readWriteValueListener
    }

    /**
     * Initialize server socket
     * Create Socket
     * listen - establish connection
     * @param port - port number
     * @param timeOut - the specified timeout in millisecond
     * @param connectResultListener - Connection Result Interface get result of success or failed
     * @see ConnectResultListener
     */
    fun connectWithPort(
        context: Context,
        port: Int,
        timeOut: Int? = null,
        connectResultListener: ConnectResultListener<Socket>
    ) {

        threadPolicyCall()

        if (!WifiUtils.checkWifiStatus(context)) {

            WifiUtils.enableWifi()
            Thread.sleep(5000)
            if (!WifiUtils.checkWifiStatus(context)) return
        }

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
                    connectResultListener.onSuccess(
                        Utils.concatDateAndTime("Connected Client $clientId"),
                        connectedMap
                    )
                    clientId++

                    val ct1 = ClientHandlerNew(socket!!, connectResultListener)
                    ct1.start()
                }
            } catch (e: SocketException) {
                printLog("connection failed Socket closed $e")
            } catch (e: IOException) {
                printLog("Connect failed $e")
                connectResultListener.onFailure(Utils.concatDateAndTime("Can't connect"))
            }
        }
        thread.start()
    }

    //
    //region methods for write value
    //
    /**
     * Socket Output - wrap DataOutPutStream
     * write String value to individual client using unique name
     * @param name - send value to name of client
     * @param string - value of write string
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeStringValueIndividual(
        name: String,
        string: String,
        readWriteValueListener: ReadWriteValueListener<String>
    ) {

        connectedMap?.let { it ->
            val mKey = it.filterKeys { it == name }
            if (mKey.containsKey(name)) {
                socket = it[name]
            }
        } ?: printLog("No any connected client")

        try {
            val dataOutStream = DataOutputStream(socket?.getOutputStream())
            dataOutStream.writeInt(1)
            dataOutStream.writeUTF(string)
            printLog("outputWriteString successful")
            readWriteValueListener.onSuccess("Write String successful", string)
            dataOutStream.flush()
        } catch (e: java.lang.Exception) {
            printLog("outputWriteString $e")
            readWriteValueListener.onFailure("Write String Failed $e")
        }
    }

    /**
     * Socket Output - wrap DataOutPutStream
     * write String to all client
     * @param string - value of write string
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeStringValueAll(
        string: String,
        readWriteValueListener: ReadWriteValueListener<String>
    ) {

        connectedMap?.let {
            for (key in it.keys) {
                socket = it[key]
                try {
                    val dataOutStream = DataOutputStream(socket?.getOutputStream())
                    dataOutStream.writeInt(1)
                    dataOutStream.writeUTF(string)
                    printLog("outputWriteString successful")
                    readWriteValueListener.onSuccess("Write String successful", string)
                    dataOutStream.flush()
                } catch (e: java.lang.Exception) {
                    printLog("outputWriteString $e")
                    readWriteValueListener.onFailure("Write String Failed $e")
                }
            }
        } ?: printLog("No any connected client")
    }

    /**
     * write Int value to individual client using unique name
     * @param name - send value to name of client
     * @param value - Integer value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeIntValueIndividual(
        name: String,
        value: Int,
        readWriteValueListener: ReadWriteValueListener<Int>
    ) {

        connectedMap?.let { it ->
            val mKey = it.filterKeys { it == name }
            if (mKey.containsKey(name)) {
                socket = it[name]
            }
        } ?: printLog("No any connected client")

        try {
            val dStream = DataOutputStream(socket?.getOutputStream())
            dStream.writeInt(2)
            dStream.writeInt(value)
            printLog("writeInt successful")
            readWriteValueListener.onSuccess("Write Int value success", value)
            dStream.flush()
        } catch (e: java.lang.Exception) {
            printLog("writeIntValue $e")
            readWriteValueListener.onFailure("Write Int value failed $e")
        }
    }

    /**
     * write Int value to All client
     * @param value - Integer value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeIntValueAll(value: Int, readWriteValueListener: ReadWriteValueListener<Int>) {

        connectedMap?.let {
            for (key in it.keys) {

                socket = it[key]

                try {
                    val dStream = DataOutputStream(socket?.getOutputStream())
                    dStream.writeInt(2)
                    dStream.writeInt(value)
                    printLog("writeInt successful")
                    readWriteValueListener.onSuccess("Write Int value success", value)
                    dStream.flush()
                } catch (e: java.lang.Exception) {
                    printLog("writeIntValue $e")
                    readWriteValueListener.onFailure("Write Int value failed $e")
                }
            }
        } ?: printLog("No any connected client")


    }

    /**
     * write Float value to individual client using unique name
     * @param name - send value to name of client
     * @param value - Float value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeFloatValueIndividual(
        name: String,
        value: Float,
        readWriteValueListener: ReadWriteValueListener<Float>
    ) {

        connectedMap?.let { it ->
            val mKey = it.filterKeys { it == name }
            if (mKey.containsKey(name)) {
                socket = it[name]
            }
        } ?: printLog("No any connected client")

        try {
            val dStream = DataOutputStream(socket?.getOutputStream())
            dStream.writeInt(3)
            dStream.writeFloat(value)
            printLog("writeFloatValue successful")
            readWriteValueListener.onSuccess("Write float value success", value)
            dStream.flush()
        } catch (e: java.lang.Exception) {
            printLog("writeFloatValue $e")
            readWriteValueListener.onFailure("Write Float value failed $e")
        }

    }

    /**
     * write Float value to all client
     * @param value - Float value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeFloatValueAll(value: Float, readWriteValueListener: ReadWriteValueListener<Float>) {

        connectedMap?.let {
            for (key in it.keys) {

                socket = it[key]

                try {
                    val dStream = DataOutputStream(socket?.getOutputStream())
                    dStream.writeInt(3)
                    dStream.writeFloat(value)
                    printLog("writeFloatValue successful")
                    readWriteValueListener.onSuccess("Write float value success", value)
                    dStream.flush()
                } catch (e: java.lang.Exception) {
                    printLog("writeFloatValue $e")
                    readWriteValueListener.onFailure("Write Float value failed $e")
                }
            }
        } ?: printLog("No any connected client")
    }

    /**
     * write Double value to individual client using unique name
     * @param name - send value to name of client
     * @param value - Double value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeDoubleValueIndividual(
        name: String,
        value: Double,
        readWriteValueListener: ReadWriteValueListener<Double>
    ) {

        connectedMap?.let { it ->
            val mKey = it.filterKeys { it == name }
            if (mKey.containsKey(name)) {
                socket = it[name]
            }
        } ?: printLog("No any connected client")

        try {
            val dStream = DataOutputStream(socket?.getOutputStream())
            dStream.writeInt(4)
            dStream.writeDouble(value)
            printLog("writeDoubleValue $value")
            readWriteValueListener.onSuccess("Write double value success", value)
            dStream.flush()
        } catch (e: java.lang.Exception) {
            printLog("writeDoubleValue $e")
            readWriteValueListener.onFailure("Write double value failed $e")
        }
    }

    /**
     * write Double value to all client
     * @param value - Double value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeDoubleValueAll(value: Double, readWriteValueListener: ReadWriteValueListener<Double>) {

        connectedMap?.let {
            for (key in it.keys) {

                socket = it[key]

                try {
                    val dStream = DataOutputStream(socket?.getOutputStream())
                    dStream.writeInt(4)
                    dStream.writeDouble(value)
                    printLog("writeDoubleValue $value")
                    readWriteValueListener.onSuccess("Write double value success", value)
                    dStream.flush()
                } catch (e: java.lang.Exception) {
                    printLog("writeDoubleValue $e")
                    readWriteValueListener.onFailure("Write double value failed $e")
                }
            }
        } ?: printLog("No any connected client")
    }

    /**
     * write Long value to individual client using unique name
     * @param name - send value to name of client
     * @param value - Long value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeLongValueIndividual(
        name: String,
        value: Long,
        readWriteValueListener: ReadWriteValueListener<Long>
    ) {

        connectedMap?.let { it ->
            val mKey = it.filterKeys { it == name }
            if (mKey.containsKey(name)) {
                socket = it[name]
            }
        } ?: printLog("No any connected client")

        try {
            val dStream = DataOutputStream(socket?.getOutputStream())
            dStream.writeInt(5)
            dStream.writeLong(value)
            printLog("writeLongValue $value")
            readWriteValueListener.onSuccess("Write Long value success", value)
            dStream.flush()
        } catch (e: java.lang.Exception) {
            printLog("writeLongValue $e")
            readWriteValueListener.onFailure("Write Long value failed $e")
        }
    }

    /**
     * write Long value to value to all client
     * @param value - Long value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeLongValueAll(value: Long, readWriteValueListener: ReadWriteValueListener<Long>) {

        connectedMap?.let {
            for (key in it.keys) {

                socket = it[key]

                try {
                    val dStream = DataOutputStream(socket?.getOutputStream())
                    dStream.writeInt(5)
                    dStream.writeLong(value)
                    printLog("writeLongValue $value")
                    readWriteValueListener.onSuccess("Write Long value success", value)
                    dStream.flush()
                } catch (e: java.lang.Exception) {
                    printLog("writeLongValue $e")
                    readWriteValueListener.onFailure("Write Long value failed $e")
                }
            }
        } ?: printLog("No any connected client")
    }

    /**
     * write short value to individual client using unique name
     * @param name - send value to name of client
     * @param value - Integer value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeShortValueIndividual(
        name: String,
        value: Int,
        readWriteValueListener: ReadWriteValueListener<Int>
    ) {

        connectedMap?.let { it ->
            val mKey = it.filterKeys { it == name }
            if (mKey.containsKey(name)) {
                socket = it[name]
            }
        } ?: printLog("No any connected client")

        try {
            val dStream = DataOutputStream(socket?.getOutputStream())
            dStream.writeInt(6)
            dStream.writeShort(value)
            printLog("writeShortValue $value")
            readWriteValueListener.onSuccess("Write Short value success", value)
            dStream.flush()
        } catch (e: java.lang.Exception) {
            printLog("writeShortValue $e")
            readWriteValueListener.onFailure("Write Short value failed $e")
        }
    }

    /**
     * write short value value to all client
     * @param value - Integer value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeShortValueAll(value: Int, readWriteValueListener: ReadWriteValueListener<Int>) {

        connectedMap?.let {
            for (key in it.keys) {

                socket = it[key]

                try {
                    val dStream = DataOutputStream(socket?.getOutputStream())
                    dStream.writeInt(6)
                    dStream.writeShort(value)
                    printLog("writeShortValue $value")
                    readWriteValueListener.onSuccess("Write Short value success", value)
                    dStream.flush()
                } catch (e: java.lang.Exception) {
                    printLog("writeShortValue $e")
                    readWriteValueListener.onFailure("Write Short value failed $e")
                }
            }
        } ?: printLog("No any connected client")
    }

    /**
     * write Boolean value to individual client using unique name
     * @param name - send value to name of client
     * @param value - Boolean value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeBooleanValueIndividual(
        name: String,
        value: Boolean,
        readWriteValueListener: ReadWriteValueListener<Boolean>
    ) {

        connectedMap?.let { it ->
            val mKey = it.filterKeys { it == name }
            if (mKey.containsKey(name)) {
                socket = it[name]
            }
        } ?: printLog("No any connected client")
        try {
            val dStream = DataOutputStream(socket?.getOutputStream())
            dStream.writeInt(7)
            dStream.writeBoolean(value)
            printLog("writeBooleanValue successful $value")
            readWriteValueListener.onSuccess("write Boolean value success", value)
            dStream.flush()
        } catch (e: java.lang.Exception) {
            printLog("writeBooleanValue $e")
            readWriteValueListener.onFailure("Write Boolean value failed $e")
        }

    }

    /**
     * write Boolean value to all client
     * @param value - Boolean value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeBooleanValueAll(
        value: Boolean,
        readWriteValueListener: ReadWriteValueListener<Boolean>
    ) {

        connectedMap?.let {
            for (key in it.keys) {

                socket = it[key]

                try {
                    val dStream = DataOutputStream(socket?.getOutputStream())
                    dStream.writeInt(7)
                    dStream.writeBoolean(value)
                    printLog("writeBooleanValue successful $value")
                    readWriteValueListener.onSuccess("write Boolean value success", value)
                    dStream.flush()
                } catch (e: java.lang.Exception) {
                    printLog("writeBooleanValue $e")
                    readWriteValueListener.onFailure("Write Boolean value failed $e")
                }
            }
        } ?: printLog("No any connected client")
    }

    /**
     * write object value to individual client using unique name
     * @param name - send value to name of client
     * @param hashtable - value of write object value to individual client using unique name
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeObjectFromSocketIndividual(
        name: String,
        hashtable: Hashtable<*, *>,
        readWriteValueListener: ReadWriteValueListener<Hashtable<*, *>>
    ) {

        connectedMap?.let { it ->
            val mKey = it.filterKeys { it == name }
            if (mKey.containsKey(name)) {
                socket = it[name]
            }
        } ?: printLog("No any connected client")

        try {
            val osStream = ObjectOutputStream(socket?.getOutputStream())
            osStream.writeInt(8)
            osStream.writeObject(hashtable)
            printLog("writeObject successful")
            readWriteValueListener.onSuccess("Write object success", hashtable)
            osStream.flush()
        } catch (e: java.lang.Exception) {
            printLog("writeObject $e")
            readWriteValueListener.onFailure("Write object failed $e")
        }
    }

    /**
     * write object value to to all client
     * @param hashtable - value of write object value to individual client using unique name
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeObjectFromSocketAll(
        hashtable: Hashtable<*, *>,
        readWriteValueListener: ReadWriteValueListener<Hashtable<*, *>>
    ) {

        connectedMap?.let {
            for (key in it.keys) {

                socket = it[key]

                try {
                    val osStream = ObjectOutputStream(socket?.getOutputStream())
                    osStream.writeInt(8)
                    osStream.writeObject(hashtable)
                    printLog("writeObject successful")
                    readWriteValueListener.onSuccess("Write object success", hashtable)
                    osStream.flush()
                } catch (e: java.lang.Exception) {
                    printLog("writeObject $e")
                    readWriteValueListener.onFailure("Write object failed $e")
                }
            }
        } ?: printLog("No any connected client")


    }

    /**
     * send byte array to individual client using unique name
     * @param name - send value to name of client
     * @param sendByteArray - send ByteArray value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun sendBytesIndividual(
        name: String,
        sendByteArray: ByteArray,
        readWriteValueListener: ReadWriteValueListener<ByteArray>
    ) {
        val start = 0
        val len = sendByteArray.size
        try {
            require(len >= 0) { readWriteValueListener.onFailure("Negative length not allowed") }
            if (start < 0 || start >= sendByteArray.size) throw IndexOutOfBoundsException("Out of bounds: $start")

            sendByte(name, sendByteArray, start) { string, exceptions ->
                if (exceptions != null) {
                    readWriteValueListener.onFailure("Send byteArray failed $exceptions")
                } else {
                    readWriteValueListener.onSuccess(string, sendByteArray)
                }
            }

        } catch (e: java.lang.Exception) {
            printLog("sendBytes failed $e")
            readWriteValueListener.onFailure("Send byteArray failed $e")
        }
    }

    /**
     * send byte array value to all client
     * @param sendByteArray - send ByteArray value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun sendBytesAll(
        sendByteArray: ByteArray,
        readWriteValueListener: ReadWriteValueListener<ByteArray>
    ) {
        val start = 0
        val len = sendByteArray.size
        try {
            require(len >= 0) { readWriteValueListener.onFailure("Negative length not allowed") }
            if (start < 0 || start >= sendByteArray.size) throw IndexOutOfBoundsException("Out of bounds: $start")

            sendByte(sendByteArray = sendByteArray, startValue = start) { string, exceptions ->
                if (exceptions != null) {
                    readWriteValueListener.onFailure("Send byteArray failed $exceptions")
                } else {
                    readWriteValueListener.onSuccess(string, sendByteArray)
                }
            }

        } catch (e: java.lang.Exception) {
            printLog("sendBytes failed $e")
            readWriteValueListener.onFailure("Send byteArray failed $e")
        }
    }

    /**
     * send byte array of start index and end index
     * @param sendByteArray - send ByteArray value to individual client using unique name
     * @param name - send value to name of client
     * @param start - start Index value
     * @param end - end Index value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun sendBytesIndividual(
        name: String,
        sendByteArray: ByteArray,
        start: Int,
        end: Int,
        readWriteValueListener: ReadWriteValueListener<ByteArray>
    ) {
        try {
            require(end >= 0) { readWriteValueListener.onFailure("Negative length not allowed") }
            if (start < 0 || start >= sendByteArray.size) throw IndexOutOfBoundsException("Out of bounds: $start")

            sendByte(
                name = name,
                sendByteArray = sendByteArray,
                startValue = start,
                endValue = end
            ) { string, exceptions ->
                if (exceptions != null) {
                    readWriteValueListener.onFailure("Send byteArray failed $exceptions")
                } else {
                    readWriteValueListener.onSuccess(string, sendByteArray)
                }
            }

        } catch (e: IOException) {
            printLog("sendBytes failed $e")
            readWriteValueListener.onFailure("Send byteArray failed $e")
        }
    }

    /**
     * send byte array of start index and end index value to all client
     * @param sendByteArray - send ByteArray value to all client
     * @param start - start Index value
     * @param end - end Index value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun sendBytesAll(
        sendByteArray: ByteArray,
        start: Int,
        end: Int,
        readWriteValueListener: ReadWriteValueListener<ByteArray>
    ) {
        try {
            require(end >= 0) { readWriteValueListener.onFailure("Negative length not allowed") }
            if (start < 0 || start >= sendByteArray.size) throw IndexOutOfBoundsException("Out of bounds: $start")

            sendByte(
                sendByteArray = sendByteArray,
                startValue = start,
                endValue = end
            ) { string, exceptions ->
                if (exceptions != null) {
                    readWriteValueListener.onFailure("Send byteArray failed $exceptions")
                } else {
                    readWriteValueListener.onSuccess(string, sendByteArray)
                }
            }

        } catch (e: IOException) {
            printLog("sendBytes failed $e")
            readWriteValueListener.onFailure("Send byteArray failed $e")
        }
    }

    private fun sendByte(
        name: String? = null,
        sendByteArray: ByteArray,
        startValue: Int,
        endValue: Int? = null,
        mCallback: (String, IOException?) -> Unit
    ) {

        if (name != null) {
            connectedMap?.let { it ->
                val mKey = it.filterKeys { it == name }
                if (mKey.containsKey(name)) {
                    socket = it[name]
                }
            } ?: printLog("No any connected client")

            try {
                val len: Int = if (endValue != null) {
                    endValue - startValue
                } else {
                    sendByteArray.size
                }
                printLog("Send Bytes length is $len")

                val out = socket?.getOutputStream()
                val dos = DataOutputStream(out)

                dos.writeInt(10)

                dos.writeInt(len)
                if (len > 0) {
                    dos.write(sendByteArray, startValue, len)
                }

                printLog("sendByte array successful")
                mCallback.invoke("sendByte array successful", null)

                dos.flush()
            } catch (e: IOException) {
                printLog("sendByte failed $e")
                mCallback.invoke("Exceptions", e)

            }
        } else {
            connectedMap?.let {
                for (key in it.keys) {

                    socket = it[key]

                    try {
                        val len: Int = if (endValue != null) {
                            endValue - startValue
                        } else {
                            sendByteArray.size
                        }
                        printLog("Send Bytes length is $len")

                        val out = socket?.getOutputStream()
                        val dos = DataOutputStream(out)

                        dos.writeInt(10)

                        dos.writeInt(len)
                        if (len > 0) {
                            dos.write(sendByteArray, startValue, len)
                        }

                        printLog("sendByte array successful")
                        mCallback.invoke("sendByte array successful", null)

                        dos.flush()
                    } catch (e: IOException) {
                        printLog("sendByte failed $e")
                        mCallback.invoke("Exceptions", e)

                    }
                }
            } ?: printLog("No any connected client")
        }
    }

    //
    //endregion not used not used
    //

    //
    //region read new methods not used
    //

    /**
     * Socket input - wrap DataInputStream
     * read string
     * receive value between symbol (symbol - * and #) if it's contains in string
     * @param socket
     */
    private fun readStringNew(
        socket: Socket
    ) {
        try {
            val dataInPutStream = DataInputStream(socket.getInputStream())
            val stringData = dataInPutStream.readUTF().trim()
            printLog("inputReadString $stringData")

            if (stringData.contains("*") && stringData.contains("#")) {
                var starIndex: Int? = null
                var hashIndex: Int? = null

                var i = 0
                while (i < stringData.length) {
                    printLog("Read String Value ${stringData[i]}")
                    if (stringData[i].toString() == "*") {
                        starIndex = i
                        i++
                        continue
                    }

                    if (stringData[i].toString() == "#") {
                        hashIndex = i
                    }

                    starIndex?.let { s ->
                        hashIndex?.let { h ->
                            if (s < h) {
                                printLog("s < h")

                                val result = stringData.substring(s + 1, h)
                                printLog("Read String Value $result")
                                readWriteValueListener?.onSuccess(
                                    "Read String successful",
                                    result
                                )

                                starIndex = null
                                hashIndex = null
                            } else {
                                printLog("s > h")

                                hashIndex = null
                                printLog(hashIndex.toString())

                            }
                        }
                    }
                    i++
                }
            } else {
                printLog("Read String Value $stringData")
                readWriteValueListener?.onSuccess(
                    "Read String successful",
                    stringData
                )
            }
        } catch (e: java.lang.Exception) {
            printLog("inputReadString $e")
            readWriteValueListener?.onFailure("Read String Failed $e")
        }
    }

    /**
     * read Int value
     * @param socket
     */
    private fun readIntValueNew(socket: Socket) {
        try {
            val dis = DataInputStream(socket.getInputStream())
            val value = dis.readInt()
            printLog("readIntValue $value")
            readWriteValueListener?.onSuccess("Read Int value success", value.toString())
        } catch (e: java.lang.Exception) {
            printLog("readIntValue $e")
            readWriteValueListener?.onFailure("Read Int value failed $e")
        }
    }

    /**
     * read float value
     * @param socket
     */
    private fun readFloatValueNew(socket: Socket) {
        try {
            val dis = DataInputStream(socket.getInputStream())
            val fValue = dis.readFloat()
            printLog("readFloatValue $fValue")
            readWriteValueListener?.onSuccess("Read float value success", fValue.toString())
        } catch (e: java.lang.Exception) {
            printLog("readFloatValue $e")
            readWriteValueListener?.onFailure("Read Float value failed $e")
        }
    }

    /**
     * read Double value
     * @param socket
     */
    private fun readDoubleValueNew(socket: Socket) {
        try {
            val dis = DataInputStream(socket.getInputStream())
            val value = dis.readDouble()
            printLog("readDoubleValue $value")
            readWriteValueListener?.onSuccess("Read double value success", value.toString())
        } catch (e: java.lang.Exception) {
            printLog("readDoubleValue $e")
            readWriteValueListener?.onFailure("Read double value failed $e")
        }
    }

    /**
     * read Long value
     * @param socket
     */
    private fun readLongValueNew(socket: Socket) {
        try {
            val dis = DataInputStream(socket.getInputStream())
            val value = dis.readLong()
            printLog("readLongValue $value")
            readWriteValueListener?.onSuccess("Read Long value success", value.toString())
        } catch (e: java.lang.Exception) {
            printLog("readLongValue $e")
            readWriteValueListener?.onFailure("Read Long value failed $e")
        }
    }

    /**
     * read short value
     * @param socket
     */
    private fun readShortValueNew(socket: Socket) {
        try {
            val dis = DataInputStream(socket.getInputStream())
            val value = dis.readShort()
            printLog("readShortValue $value")
            readWriteValueListener?.onSuccess("Read Short value success", value.toString())
        } catch (e: java.lang.Exception) {
            printLog("readShortValue $e")
            readWriteValueListener?.onFailure("Read Short value failed $e")
        }
    }

    /**
     * read Boolean value
     * @param socket
     */
    private fun readBooleanValueNew(
        socket: Socket
    ) {
        try {
            val dis = DataInputStream(socket.getInputStream())
            val value = dis.readBoolean()
            printLog("readBooleanValue successful $value")
            readWriteValueListener?.onSuccess("Read Boolean value success", value.toString())
        } catch (e: java.lang.Exception) {
            printLog("readBooleanValue $e")
            readWriteValueListener?.onFailure("Read Boolean value failed $e")
        }
    }

    /**
     * read Object from socket
     * @param socket
     */
    private fun readObjectFromSocketNew(socket: Socket) {
        try {
            val ois = ObjectInputStream(socket.getInputStream())
            val hash: Hashtable<*, *> = ois.readObject() as Hashtable<*, *>
            printLog("readObjectFromSocket $hash")
            readWriteValueListener?.onSuccess("Read object successful", hash.toString())
        } catch (e: java.lang.Exception) {
            printLog("readObjectFromSocket $e")
            readWriteValueListener?.onFailure("Read object failed $e")
        }
    }

    /**
     * Set file name and size
     * @param name - file name for where to stored file
     * @param byteSize - Size of bytes
     */
    fun setFileNameAndSize(name: String, byteSize: Int) {
        fileName = name
        fileSize = byteSize
    }

    /**
     * received file (byte)
     * If file name and size is null than throw null pointer exceptions
     * For set name and size
     * @see setFileNameAndSize
     */
    private fun receivedFileNew(socket: Socket) {
        if (fileName == null && fileSize == null) {
            throw NullPointerException("File name and size must be set by calling method ${TCPServerService::class.java.simpleName}.setFileNameAndSize(filename, size)")
        }

        try {
            val inputStream = socket.getInputStream()
            val out = FileOutputStream(fileName)
            val bytes = ByteArray(fileSize!!)

            var count: Int
            while (inputStream?.read(bytes)!!.also { count = it } > 0) {
                out.write(bytes, 0, count)
            }
            printLog("ReceivedFile successful")
            readWriteValueListener?.onSuccess("ReceivedFile successful", fileName)
            out.close()
        } catch (e: java.lang.Exception) {
            printLog("receivedFile $e")
            readWriteValueListener?.onFailure("ReceivedFile Failed $e")
        }
    }

    /**
     * Set packet length for received bytes in packet
     * @param pLength - length of packet
     */
    fun setPacketLength(pLength: Int) {
        packetLength = pLength
    }

    /**
     * receive byte array in given packet length or total values
     * If packet length is not null than receive bytes in length
     * For set packet length call methods
     * @see setPacketLength
     *
     * if byte array value is divide in packet length and after remain some values than @throws ArrayIndexOutOfBound
     */

    private fun receiveBytesNew(socket: Socket) {
        try {
            val inputStream = socket.getInputStream()
            val dis = DataInputStream(inputStream)
            val len = dis.readInt()
            val data = ByteArray(len)

            if (packetLength != null) {
                var count = 0

                while (len > 0 && count < len) {

                    dis.read(data, count, packetLength!!)
                    count += packetLength!!

                    printLog("Received byte ${data.toString(Charset.defaultCharset())}")

                    readWriteValueListener?.onSuccess(
                        "Received bytes successful",
                        data.toString(Charset.defaultCharset())
                    )
                }

            } else {
                if (len > 0) {
                    dis.readFully(data)
                }

                val stringData = data.toString(Charset.defaultCharset())

                if (stringData.contains("*") && stringData.contains("#")) {
                    var starIndex: Int? = null
                    var hashIndex: Int? = null

                    var i = 0
                    while (i < stringData.length) {
                        printLog("Read String Value ${stringData[i]}")
                        if (stringData[i].toString() == "*") {
                            starIndex = i
                            i++
                            continue
                        }

                        if (stringData[i].toString() == "#") {
                            hashIndex = i
                        }

                        starIndex?.let { s ->
                            hashIndex?.let { h ->
                                if (s < h) {
                                    printLog("s < h")

                                    val result = stringData.substring(s + 1, h)
                                    printLog("Read String Value $result")
                                    readWriteValueListener?.onSuccess(
                                        "Read String successful",
                                        result
                                    )

                                    starIndex = null
                                    hashIndex = null
                                } else {
                                    printLog("s > h")

                                    hashIndex = null
                                    printLog(hashIndex.toString())

                                }
                            }
                        }
                        i++
                    }
                } else {
                    printLog("Read String Value $stringData")
                    readWriteValueListener?.onSuccess(
                        "Read String successful",
                        stringData
                    )
                }
            }

        } catch (e: Exception) {
            printLog("receiveBytes failed $e")
            readWriteValueListener?.onFailure("receivedBytes failed $e")
        }

    }

    /**
     * receive default value
     * @see ReadWriteValueListener
     */
    fun receivedDefaultValues() {
        try {
            val inputStream = socket?.getInputStream()

            val data = ByteArray(1024)
            val readCount = inputStream?.read(data)
            printLog("Read count $readCount")

            val stringData = Utils.trimByteArray(data).toString(Charset.defaultCharset())
            Log.e("Default true", stringData)

            if (stringData.contains("*") && stringData.contains("#")) {
                var starIndex: Int? = null
                var hashIndex: Int? = null

                var i = 0
                while (i < stringData.length) {
                    printLog("Read String Value ${stringData[i]}")
                    if (stringData[i].toString() == "*") {
                        starIndex = i
                        i++
                        continue
                    }

                    if (stringData[i].toString() == "#") {
                        hashIndex = i
                    }

                    starIndex?.let { s ->
                        hashIndex?.let { h ->
                            if (s < h) {
                                printLog("s < h")

                                val result = stringData.substring(s + 1, h)
                                printLog("Read String Value $result")
                                readWriteValueListener?.onSuccess(
                                    "Read String successful",
                                    result
                                )

                                starIndex = null
                                hashIndex = null
                            } else {
                                printLog("s > h")

                                hashIndex = null
                                printLog(hashIndex.toString())

                            }
                        }
                    }
                    i++
                }
            } else {
                printLog("Read String Value $stringData")
                readWriteValueListener?.onSuccess(
                    "Read String successful",
                    stringData
                )
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("Default false", e.toString())
        }
    }

    //
    //endregion
    //

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
        val socket: Socket,
        val connectResultListener: ConnectResultListener<Socket>
    ) : Thread() {
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
                exceptionHandle(socket, connectResultListener)
            } catch (e: Exception) {
                printLog("Exception in ClientHandler $e")
                exceptionHandle(socket, connectResultListener)
            } finally {
                printLog("Finally in ClientHandler")
                exceptionHandle(socket, connectResultListener)
            }
        }
    }

    private fun exceptionHandle(
        socket: Socket,
        connectResultListener: ConnectResultListener<Socket>
    ) {
        if (!socket.isClosed) {
            connectedMap?.let { it ->

                val mKey = it.filterValues { it == socket }
                Log.e("Binjal", "key $mKey")

                if (mKey.containsValue(socket)) {
                    it.values.remove(socket)

                    Log.e("Binjal", "hh $it")

                    connectResultListener.onSuccess(
                        (Utils.concatDateAndTime("${mKey.keys} is disconnected")),
                        it
                    )
                    FastSave.getInstance().deleteValue(Utils.REMAINING_BYTE_ARRAY_STRING + mKey.filterValues { it == socket }.keys)
                    FastSave.getInstance().deleteValue(Utils.REMAINING_STRING_DATA + mKey.filterValues { it == socket }.keys)

                } else {
                    connectResultListener.onFailure("Disconnect failed")
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
            val data = ByteArray(1024)
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
/*
    private fun readValueUsingPacketLength(socket: Socket) {
        val packetLength: Int = SettingsService.getPacketLength()
        printLog("packetLength is $packetLength")

        try {
            val inputStream = socket.getInputStream()

            var data = ByteArray(1024)
            var dummyArray = ByteArray(1024)
            var isFirstSet = true

            if (packetLength != 0) {
                var count = 0

                // read byte array up to set packet length [count] and stored in data
                while ((inputStream?.read(data, count, packetLength)) != -1) {
                    // Increase count for read new byte array
                    count += packetLength

                    var isRemainingExists = FastSave.getInstance().isKeyExists(Utils.REMAINING_BYTE_ARRAY_STRING + connectedMap?.filterValues { it == socket }?.keys)
                    Log.e(
                        "Binjal1",
                        "remainingByteArray is ${FastSave.getInstance().isKeyExists(Utils.REMAINING_BYTE_ARRAY_STRING + connectedMap?.filterValues { it == socket }?.keys)}"
                    )
                    */
/*Log.e(
                        "Binjal1",
                        "remainingByteArray is ${remainingByteArray.toString(Charset.defaultCharset())}"
                    )*//*


                    */
/**
                     * Check old remaining byte array is empty
                     * if not empty than fill all read byte array append with remaining byte array
                     * and after get data array in remainingBytearray [packetLength]
                     * else data = read all data
                     *//*


                    if (remainingByteArray.isNotEmpty()) {
                        remainingByteArray += Utils.trimByteArray(data)
                        isFirstSet = false

                        data = remainingByteArray.take(packetLength).toByteArray()

                        Log.e(
                            "Binjal2",
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
                            Log.e(
                                "Binjal3",
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
                        "Binjal1",
                        "Key exists is ${FastSave.getInstance().isKeyExists(Utils.REMAINING_BYTE_ARRAY_STRING + connectedMap?.filterValues { it == socket }?.keys)}"
                    )
                    /*Log.e(
                        "Binjal1",
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
                            "Binjal2",
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
                                "Binjal3",
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
   /* private fun readValueBetweenStartAndEndBits(socket: Socket) {
        val startBit = SettingsService.getStartBit()
        val endBits = SettingsService.getEndBit()

        val inputStream = socket.getInputStream()
        var stringData = ""
        var finalResultString = ""
        var data = ByteArray(1024)

        while ((inputStream?.read(data)) != -1) {
//            if (FastSave.getInstance().isKeyExists(Utils.REMAINING_STRING_DATA)) stringData = FastSave.getInstance().getString(Utils.REMAINING_STRING_DATA, "")
            try {
                if (!remainingStringData.isNullOrBlank()) {
                    remainingStringData += Utils.trimByteArray(data)
                        .toString(Charset.defaultCharset())

                    stringData = remainingStringData!!
                    remainingStringData = ""
                    Log.e("Binjal21", "remaining StringData not null blank $stringData")

                } else {
                    stringData = Utils.trimByteArray(data).toString(Charset.defaultCharset())
                    Log.e("Binjal21", "remaining StringData null blank $stringData")
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
                                i++
                                continue
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

                                        when {
                                            SettingsService.isDisplayPlainText() -> {
                                                printLog("Read Value Between Start And End Bits plain $finalResultString")
                                                readWriteValueListener?.onSuccess(
                                                    "Read Value Between Start And End Bits",
                                                    Utils.concatDateAndTime(finalResultString)
                                                )
                                            }
                                            SettingsService.isDisplayDecimal() -> {
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
                                            SettingsService.isDisplayHexString() -> {
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

                                    if (subStr.contains(startBit)) {
                                        remainingStringData = subStr
                                        Log.e(
                                            "Binjal*",
                                            "remainingStringData data is $remainingStringData"
                                        )
                                    }

                                    startBitIndex = null
                                    endBitIndex = null
                                    isSetStartIndex = false
                                } else {
                                    printLog("s > e")

                                    endBitIndex = null
                                    printLog(endBitIndex.toString())
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
                    Log.e("Binjal*", "remainingStringData data is $remainingStringData")
                }
            } catch (e: java.lang.Exception) {
                printLog("Exception in Read Value Between Start And End Bits $e")
            }
        }
    }*/

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
                    Log.e("Binjal21", "remaining StringData not null blank $stringData")

                } else {
                    stringData = Utils.trimByteArray(data).toString(Charset.defaultCharset())
                    Log.e("Binjal21", "remaining StringData null blank $stringData")
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
                                            "Binjal*",
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
                                            "Binjal*",
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
                    Log.e("Binjal*", "remainingStringData data is $remainingStringData")
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
//                        FastSave.getInstance().deleteValue(Utils.REMAINING_BYTE_ARRAY_STRING + connectedMap?.filterValues { it == socket }?.keys)
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
//            remainingStringData = null
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
        if (isEnableLog) Log.e(logTag, message)
    }

    /**
     * Thread policy for resolve networkMainThread Exceptions
     */
    private fun threadPolicyCall() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

}


