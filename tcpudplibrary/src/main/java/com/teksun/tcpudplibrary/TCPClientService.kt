package com.teksun.tcpudplibrary

import android.annotation.SuppressLint
import android.content.Context
import android.os.StrictMode
import android.util.Log
import com.teksun.tcpudplibrary.listener.CloseSocketListener
import com.teksun.tcpudplibrary.listener.ConnectCResultListener
import com.teksun.tcpudplibrary.listener.ReadWriteValueListener
import java.io.*
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.nio.charset.Charset
import java.util.*

@SuppressLint("StaticFieldLeak")
object TCPClientService {
    private val logTag = TCPClientService::class.java.simpleName
    private var socket: Socket? = null
    private var isEnableLog = false
    private var readWriteValueListener: ReadWriteValueListener<String>? = null
    private var packetLength: Int? = null
    private var fileName: String? = null
    private var fileSize: Int? = null

    private var remainingByteArray = byteArrayOf()
    private var remainingStringData: String? = null

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
        TCPClientService.readWriteValueListener = readWriteValueListener
    }

    /**
     * Connection
     * @param ip - server ip address
     * @param port - port number
     * @param timeOut - the timeout value to be used in milliseconds
     * @param connectCResultListener - Connection Result Interface get result of success or failed
     * @see ConnectCResultListener
     */
    fun connectToAddress(
        context: Context,
        ip: String,
        port: Int,
        timeOut: Int? = null,
        connectCResultListener: ConnectCResultListener
    ) {
        if (!WifiUtils.checkWifiStatus(context)) {

            WifiUtils.enableWifi()
            Thread.sleep(1000)
            if (!WifiUtils.checkWifiStatus(context)) return
        }

        threadPolicyCall()

        try {
            if (socket != null) {
                if (socket?.isConnected!!) {
                    printLog("Already connected")
                    connectCResultListener.onSuccess("Already connected")
                    return
                }
            }

            socket = Socket()

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
                    connectCResultListener.onSuccess(Utils.concatDateAndTime("Connect"))

                    val sh = ServerHandlerNew(connectCResultListener)
                    sh.start()
                }
            }

        } catch (e: IOException) {
            printLog("Connected failed : IOException $e")
            connectCResultListener.onFailure(Utils.concatDateAndTime("Can't connect"))
        } catch (e: SocketTimeoutException) {
            printLog("Connected failed : SocketTimeoutException $e")
            connectCResultListener.onFailure(Utils.concatDateAndTime("Can't connect"))
        }
    }

    /**
     * return socket
     */
    fun getSocket(): Socket? {
        return socket
    }

    //
    //region Write value methods not used
    //

    /**
     * Socket Output - wrap DataOutPutStream
     * write String
     * @param string - value of write string
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeStringValue(string: String, readWriteValueListener: ReadWriteValueListener<String>) {
        try {
            val dStream = DataOutputStream(socket?.getOutputStream())
            dStream.writeInt(1)
            dStream.writeUTF(string)
            printLog("outputWriteString successful")
            readWriteValueListener.onSuccess("Write String successful", string)
            dStream.flush()
        } catch (e: java.lang.Exception) {
            printLog("outputWriteString $e")
            readWriteValueListener.onFailure("Write String Failed $e")
        }
    }

    /**
     * write Int value
     * @param value - Integer value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeIntValue(value: Int, readWriteValueListener: ReadWriteValueListener<Int>) {
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
     * write Float value
     * @param value - Float value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeFloatValue(value: Float, readWriteValueListener: ReadWriteValueListener<Float>) {
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
     * write Double value
     * @param value - Double value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeDoubleValue(value: Double, readWriteValueListener: ReadWriteValueListener<Double>) {
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
     * write Long value
     * @param value - Long value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeLongValue(value: Long, readWriteValueListener: ReadWriteValueListener<Long>) {
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
     * write short value
     * @param value - Integer value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeShortValue(value: Int, readWriteValueListener: ReadWriteValueListener<Int>) {
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
     * write Boolean value
     * @param value - Boolean value for write
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun writeBooleanValue(value: Boolean, readWriteValueListener: ReadWriteValueListener<Boolean>) {
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
     * write object
     * @param hashtable - value of write write string
     */
    fun writeObjectFromSocket(
        hashtable: Hashtable<*, *>,
        readWriteValueListener: ReadWriteValueListener<Hashtable<*, *>>
    ) {
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
     * send file (byte)
     * @param file - send file value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun sendFile(file: File, readWriteValueListener: ReadWriteValueListener<String>) {
        try {

            val out = socket?.getOutputStream()

            val dStream = DataOutputStream(out)
            dStream.writeInt(9)

            val length = file.length().toInt()
            val bytes = ByteArray(length)
            val inputStream: InputStream = FileInputStream(file)

            var count: Int
            while (inputStream.read(bytes).also { count = it } > 0) {
                printLog("send file count $count")

                out?.write(bytes, 0, count)
            }
            printLog("sendFile success")
            readWriteValueListener.onSuccess("Send File success", file.name)
            out?.flush()

        } catch (e: java.lang.Exception) {
            printLog("sendFile $e")
            readWriteValueListener.onFailure("Send File Failed $e")
        }
    }

    /**
     * send byte array
     * @param sendByteArray - send ByteArray value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun sendBytes(
        sendByteArray: ByteArray,
        readWriteValueListener: ReadWriteValueListener<ByteArray>
    ) {
        val start = 0
        val len = sendByteArray.size
        try {
            require(len >= 0) { readWriteValueListener.onFailure("Negative length not allowed") }
            if (start < 0 || start >= sendByteArray.size) throw IndexOutOfBoundsException("Out of bounds: $start")

            sendByte(sendByteArray, start) { string, exceptions ->
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
     * send byte array between start index and end index
     * @param sendByteArray - send ByteArray value
     * @param start - start Index value
     * @param end - end Index value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun sendBytes(
        sendByteArray: ByteArray,
        start: Int,
        end: Int,
        readWriteValueListener: ReadWriteValueListener<ByteArray>
    ) {
        try {
            require(end >= 0) { readWriteValueListener.onFailure("Negative length not allowed") }
            if (start < 0 || start >= sendByteArray.size) throw IndexOutOfBoundsException("Out of bounds: $start")

            sendByte(sendByteArray, start, end) { string, exceptions ->
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
        sendByteArray: ByteArray,
        startValue: Int,
        endValue: Int? = null,
        mCallback: (String, IOException?) -> Unit
    ) {
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

    //
    //endregion
    //

    //
    //region Read Value methods not used
    //

    /**
     * Socket input - wrap DataInputStream
     * read string
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun readStringValue(readWriteValueListener: ReadWriteValueListener<String>) {
        try {
            val dataInPutStream = DataInputStream(socket?.getInputStream())
            val stringData = dataInPutStream.readUTF().trim()
            printLog("inputReadString $stringData")

            if (stringData.contains("*") && stringData.contains("#")) {
                var starIndex: Int? = null
                var hashIndex: Int? = null

                var i = 0
                while (i < stringData.length) {
                    printLog("Read String Value ${stringData.get(i)}")
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
                                readWriteValueListener.onSuccess("Read String successful", result)

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
                readWriteValueListener.onSuccess(
                    "Read String successful",
                    stringData
                )
            }
        } catch (e: java.lang.Exception) {
            printLog("inputReadString $e")
            readWriteValueListener.onFailure("Read String Failed $e")
        }
    }

    /**
     * read Object from socket
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun readObjectFromSocket(readWriteValueListener: ReadWriteValueListener<String>) {
        try {
            val ois = ObjectInputStream(socket?.getInputStream())
            val hash: Hashtable<*, *> = ois.readObject() as Hashtable<*, *>
            printLog("readObjectFromSocket ${hash.toString()}")
            readWriteValueListener.onSuccess("Read object successful", hash.toString())
        } catch (e: java.lang.Exception) {
            printLog("readObjectFromSocket $e")
            readWriteValueListener.onFailure("Read object failed $e")
        }
    }

    /**
     * read Int value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun readIntValue(readWriteValueListener: ReadWriteValueListener<Int>) {
        try {
            val dis = DataInputStream(socket?.getInputStream())
            val value = dis.readInt()
            printLog("readIntValue $value")
            readWriteValueListener.onSuccess("Read Int value success", value)
        } catch (e: java.lang.Exception) {
            printLog("readIntValue $e")
            readWriteValueListener.onFailure("Read Int value failed $e")
        }
    }

    /**
     * read Boolean value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun readBooleanValue(readWriteValueListener: ReadWriteValueListener<Boolean>) {
        try {
            val dis = DataInputStream(socket?.getInputStream())
            val value = dis.readBoolean()
            printLog("readBooleanValue successful $value")
            readWriteValueListener.onSuccess("Read Boolean value success", value)
        } catch (e: java.lang.Exception) {
            printLog("readBooleanValue $e")
            readWriteValueListener.onFailure("Read Boolean value failed $e")
        }
    }

    /**
     * read float value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun readFloatValue(readWriteValueListener: ReadWriteValueListener<Float>) {
        try {
            val dis = DataInputStream(socket?.getInputStream())
            val fValue = dis.readFloat()
            printLog("readFloatValue $fValue")
            readWriteValueListener.onSuccess("Read float value success", fValue)
        } catch (e: java.lang.Exception) {
            printLog("readFloatValue $e")
            readWriteValueListener.onFailure("Read Float value failed $e")
        }
    }

    /**
     * read Double value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun readDoubleValue(readWriteValueListener: ReadWriteValueListener<Double>) {
        try {
            val dis = DataInputStream(socket?.getInputStream())
            val value = dis.readDouble()
            printLog("readDoubleValue $value")
            readWriteValueListener.onSuccess("Read double value success", value)
        } catch (e: java.lang.Exception) {
            printLog("readDoubleValue $e")
            readWriteValueListener.onFailure("Read double value failed $e")
        }
    }

    /**
     * read Long value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun readLongValue(readWriteValueListener: ReadWriteValueListener<Long>) {
        try {
            val dis = DataInputStream(socket?.getInputStream())
            val value = dis.readLong()
            printLog("readLongValue $value")
            readWriteValueListener.onSuccess("Read Long value success", value)
        } catch (e: java.lang.Exception) {
            printLog("readDoubleValue $e")
            readWriteValueListener.onFailure("Read Long value failed $e")
        }
    }

    /**
     * read short value
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun readShortValue(readWriteValueListener: ReadWriteValueListener<Short>) {
        try {
            val dis = DataInputStream(socket?.getInputStream())
            val value = dis.readShort()
            printLog("readShortValue $value")
            readWriteValueListener.onSuccess("Read Short value success", value)
        } catch (e: java.lang.Exception) {
            printLog("readShortValue $e")
            readWriteValueListener.onFailure("Read Short value failed $e")
        }
    }

    /**
     * received file (byte)
     * @param fileName - name of received file
     * @param byteSize - size of file bytes
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun receivedFile(
        fileName: String,
        byteSize: Int,
        readWriteValueListener: ReadWriteValueListener<String>
    ) {

        try {
            val inputStream = socket?.getInputStream()
            val out = FileOutputStream(fileName)
            val bytes = ByteArray(byteSize)

            var count: Int
            while (inputStream?.read(bytes)!!.also { count = it } > 0) {
                printLog("receivedFile count $count")

                out.write(bytes, 0, count)
            }
            printLog("ReceivedFile successful")
            readWriteValueListener.onSuccess("ReceivedFile successful", fileName)
            out.flush()
        } catch (e: java.lang.Exception) {
            printLog("receivedFile $e")
            readWriteValueListener.onFailure("ReceivedFile Failed $e")
        }
    }

    /**
     * receive byte array
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun receiveBytes(readWriteValueListener: ReadWriteValueListener<String>) {
        val inputStream = socket?.getInputStream()
        val dis = DataInputStream(inputStream)
        val len = dis.readInt()
        val data = ByteArray(len)

        if (len > 0) {
            dis.readFully(data)
        }

        val stringData = data.toString(Charset.defaultCharset())

        if (stringData.contains("*") && stringData.contains("#")) {
            var starIndex: Int? = null
            var hashIndex: Int? = null

            var i = 0
            while (i < stringData.length) {
                printLog("Read String Value ${stringData.get(i)}")
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
                            readWriteValueListener.onSuccess("Read String successful", result)

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
            readWriteValueListener.onSuccess(
                "Read String successful",
                stringData
            )
        }
    }

    /**
     * receive byte array in given packet length
     *
     * if byte array value is divide in packet length and after remain some values than @throws ArrayIndexOutOfBound
     * @param packetLength - create packet of packet length
     * @param readWriteValueListener - ReadWriteValueListener get result of success or failed
     * @see ReadWriteValueListener
     */
    fun receiveBytes(
        packetLength: Int,
        readWriteValueListener: ReadWriteValueListener<ByteArray>
    ) {
        try {
            val inputStream = socket?.getInputStream()

            val dis = DataInputStream(inputStream)
            val len = dis.readInt()
            val data = ByteArray(len)

            var count = 0

            while (len > 0 && count < len) {

                dis.read(data, count, packetLength)
                count += packetLength

                printLog("Received byte ${data.toString(Charset.defaultCharset())}")

                readWriteValueListener.onSuccess("Received bytes successful", data)
            }
        } catch (e: Exception) {
            printLog("receiveBytes failed $e")
            readWriteValueListener.onFailure("receivedBytes failed $e")
        }
    }

    //
    //endregion
    //

    //
    //region start - Read New methods
    //
    /**
     * Socket input - wrap DataInputStream
     * read string
     * receive value between symbol (symbol - * and #) if it's contains in string
     */
    private fun readStringNew() {
        try {
            val dataInPutStream = DataInputStream(socket?.getInputStream())
            val stringData = dataInPutStream.readUTF().trim()
            printLog("inputReadString $stringData")

            if (stringData.contains("*") && stringData.contains("#")) {
                var starIndex: Int? = null
                var hashIndex: Int? = null

                var i = 0
                while (i < stringData.length) {
                    printLog("Read String Value ${stringData.get(i)}")
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
                                readWriteValueListener?.onSuccess("Read String successful", result)

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
     */
    private fun readIntValueNew() {
        try {
            val dis = DataInputStream(socket?.getInputStream())
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
     */
    private fun readFloatValueNew() {
        try {
            val dis = DataInputStream(socket?.getInputStream())
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
     */
    private fun readDoubleValueNew() {
        try {
            val dis = DataInputStream(socket?.getInputStream())
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
     */
    private fun readLongValueNew() {
        try {
            val dis = DataInputStream(socket?.getInputStream())
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
     */
    private fun readShortValueNew() {
        try {
            val dis = DataInputStream(socket?.getInputStream())
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
     */
    private fun readBooleanValueNew() {
        try {
            val dis = DataInputStream(socket?.getInputStream())
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
     */
    private fun readObjectFromSocketNew() {
        try {
            val ois = ObjectInputStream(socket?.getInputStream())
            val hash: Hashtable<*, *> = ois.readObject() as Hashtable<*, *>
            printLog("readObjectFromSocket ${hash.toString()}")
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
    private fun receivedFileNew() {
        if (fileName == null && fileSize == null) {
            throw NullPointerException("File name and size must be set by calling method ${TCPClientService::class.java.simpleName}.setFileNameAndSize(filename, size)")
        }

        try {
            val inputStream = socket?.getInputStream()
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
     * @param pLength - lenth of packet
     */
    fun setPacketLength(pLength: Int) {
        packetLength = pLength
    }

    /**
     * receive byte array in given packet length or total values
     * If packet length is not null than recieve bytes in length
     * For set packet length call methods
     * @see setPacketLength
     *
     * if byte array value is divide in packet length and after remain some values than @throws ArrayIndexOutOfBound
     */

    private fun receiveBytesNew() {
        try {
            val inputStream = socket?.getInputStream()
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
                        printLog("Read String Value ${stringData.get(i)}")
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
    fun receivedDefaultValues(inputStream: InputStream?) {
        /*try {
            Log.e( "$logTag T", "receivedDefaultValues called $socket")

            val input = BufferedReader(InputStreamReader(inputStream))
            var stringData: String? = null

            while (true) {
                Log.e( "$logTag T", "inner while")

                if (input.readLine().also { stringData = it } != null) {
                    stringData?.let { data ->
                        if (data.contains("*") && data.contains("#")) {
                            var starIndex: Int? = null
                            var hashIndex: Int? = null

                            var i = 0
                            while (i < data.length) {
                                printLog("Read String Value ${data.get(i)}")
                                if (data[i].toString() == "*") {
                                    starIndex = i
                                    i++
                                    continue
                                }

                                if (data[i].toString() == "#") {
                                    hashIndex = i
                                }

                                starIndex?.let { s ->
                                    hashIndex?.let { h ->
                                        if (s < h) {
                                            printLog("s < h")

                                            val result = data.substring(s + 1, h)
                                            printLog("Read String Value $result")
                                            readWriteValueListener?.onSuccess("Read String successful", result)

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
                            printLog("Read String Value $data")
                            readWriteValueListener?.onSuccess(
                                "Read String successful",
                                data
                            )
                        }
                    } ?: printLog("No values received")
                } else {
                    printLog("No readline values received")
                }
            }
        } catch (e: java.lang.Exception) {
            printLog("ReceivedStringValue failed $e")
            readWriteValueListener?.onFailure("ReceivedStringValue failed $e")
        }*/

        printLog("receivedDefaultValues called")

        try {

            /* val reader = BufferedReader(InputStreamReader(inputStream))
            var data1 = CharArray(1024)
            var line = 0

            while ((reader.read(data1).also { line = it }) > 0) {
                Log.e("Default true", String(data1))

            }*/

            /*  val baos = ByteArrayOutputStream()
              val data = ByteArray(1024)
              var numRead = 0

              while ((inputStream?.read(data)?.also { numRead = it })!! >= 0) {
                  baos.write(data, 0, numRead)
              }

              val stringData = Utils.trimByteArray(data).toString(Charset.defaultCharset()).trim()
              Log.e("Default true", stringData)*/

//            val bufferInputStream = BufferedInputStream(inputStream)

            /* while (inputStream?.available()!! > 0 && inputStream.read() != -1) {

                 val data = ByteArray(1024)

 //                val count = inputStream?.read(data)
                 val count = bufferInputStream.read(data, 0, 1024)
                 Log.e("Count is ", count.toString())

                 val stringData = Utils.trimByteArray(data).toString(Charset.defaultCharset()).trim()
                 Log.e("Default true", stringData)*/

            val data = ByteArray(8096)
            printLog("Read count $data")

            val readCount = inputStream?.read(data)
            printLog("Read count $readCount")

            val stringData = Utils.trimByteArray(data).toString(Charset.defaultCharset())
            Log.e("Default true", stringData)

            if (stringData.contains("*") && stringData.contains("#")) {
                var starIndex: Int? = null
                var hashIndex: Int? = null

                var i = 0
                while (i < stringData.length) {
                    printLog("Read String Value ${stringData.get(i)}")
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
                                readWriteValueListener?.onSuccess("Read String successful", result)

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
//            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Default false", e.toString())

        }
    }

    //
    //endregion not used
    //

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
    //region server handler class
    //

    class ServerHandlerNew(private val connectCResultListener: ConnectCResultListener) : Thread() {
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
                exceptionHandler(connectCResultListener)
            } catch (e: Exception) {
                printLog("Exception in ServerHandler $e")
                exceptionHandler(connectCResultListener)
            } finally {
                printLog("Finally in ServerHandler")
                exceptionHandler(connectCResultListener)
            }
        }
    }

    private fun exceptionHandler(connectCResultListener: ConnectCResultListener) {
        if (socket != null && !socket?.isClosed!!) {
            socket?.close()
            socket = null
            remainingByteArray = byteArrayOf()
            remainingStringData = null
            printLog("Server is disconnected")
            connectCResultListener.onServerDisconnect(Utils.concatDateAndTime("Server is disconnected"))
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
            val data = ByteArray(1024)
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
                        "Binjal1",
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
                            Log.d(
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
                    Log.e("Binjal1", "remainingStringData not null blank $stringData")

                } else {
                    stringData = Utils.trimByteArray(data).toString(Charset.defaultCharset())
                    Log.e("Binjal2", "remainingStringData null blank $stringData")
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
                                            "Binjal*",
                                            "remainingStringData data is $remainingStringData"
                                        )
                                    }

                                    startBitIndex = null
                                    endBitIndex = null
                                    isSetStartIndex = false
                                } else {
                                    Log.d("Binjal", "s > e")

                                    val subStr = stringData.substring(endBitIndex + 1, stringData.length)

                                    remainingStringData = ""

                                    if (subStr.contains(startBit)) {
                                        remainingStringData = subStr
                                        Log.e(
                                            "Binjal*",
                                            "remainingStringData data is $remainingStringData"
                                        )
                                    }
                                    endBitIndex = null
                                }
                            } else {
                                Log.d("Binjal", "End bit null")
                            }
                        } else {
                            Log.d("Binjal", "Start bit null")
                        }
                        i++
                    }
                    data = ByteArray(1024)
                } else if (stringData.contains(startBit)) {
                    remainingStringData = ""
                    remainingStringData = stringData
                    Log.d("Binjal3", "remainingStringData data is $remainingStringData")
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