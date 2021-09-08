package com.teksun.tcpudplibrary

import android.os.StrictMode
import android.util.Log
import com.teksun.tcpudplibrary.listener.CloseSocketListener
import com.teksun.tcpudplibrary.listener.ConnectCResultListener
import com.teksun.tcpudplibrary.listener.ReadWriteValueListener
import com.teksun.tcpudplibrary.listener.ReadWriteValueListenerUDP
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException

object UDPService {
    private val logTag = UDPService::class.java.simpleName
    private var isEnableLog = false
    private var readWriteValueListener: ReadWriteValueListenerUDP<String>? = null
    private var socket: DatagramSocket? = null
    private var receivedSocket: DatagramSocket? = null
    private var serverAddress: InetAddress? = null
    private var mTargetPort: Int? = null
    private var mLocalPort: Int? = null

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
    fun setReadWriteListener(readWriteValueListener: ReadWriteValueListenerUDP<String>?) {
        this.readWriteValueListener = readWriteValueListener
    }

    /**
     * Start/listen server socket using port
     * @param targetPort - target port number for connection and send data
     * @param localPort - localPort number for received data
     * @param connectResultListener = get result of success or failed
     */
    fun connect(
        ip: String,
        targetPort: Int,
        localPort: Int,
        connectResultListener:
        ConnectCResultListener
    ) {

        mTargetPort = targetPort
        mLocalPort = localPort

        threadPolicyCall()

        val thread = Thread {
            try {
                socket = DatagramSocket()

                serverAddress = InetAddress.getByName(ip)

                connectResultListener.onSuccess(Utils.concatDateAndTime("Connect"))

                receivedData()

            } catch (e: Exception) {
                printLog("Connection failed $e")
                connectResultListener.onConnectFailure(Utils.concatDateAndTime("Can't Connect"))
            }
        }
        thread.start()
    }

    /**
     * Send data to using ip and port of server
     * @param message - send message value
     * @param readWriteValueListener - get result of success or failed in send data
     * @see ReadWriteValueListener
     */
    fun sendData(message: String, readWriteValueListener: ReadWriteValueListener<String>) {
        val thread = Thread {
            try {
                socket?.let {
                    val dp = DatagramPacket(
                        message.toByteArray(), message.length,
                        serverAddress, mTargetPort!!
                    )
                    it.send(dp)

                    printLog("Send data successful $message")
                    readWriteValueListener.onSuccess(
                        "Send data successful",
                        Utils.concatDateAndTime(message)
                    )
                } ?: printLog("Socket is closed")
            } catch (e: IOException) {
                printLog("Send data failed $e")
                readWriteValueListener.onFailure("Send data failed $e")
            }
        }
        thread.start()
    }

    /**
     * received data using local port
     */
    private fun receivedData() {
        try {
            receivedSocket = DatagramSocket(mLocalPort!!)

            val lMsg = ByteArray(1024)
            val dp = DatagramPacket(lMsg, lMsg.size, serverAddress, mLocalPort!!)
            var stringData: String? = null

            while (true) {
                if (receivedSocket?.isClosed!!) {
                    break
                }
                receivedSocket?.receive(dp)
                stringData = String(lMsg, 0, dp.length).trim()

                printLog("Received message successful $stringData")
                readWriteValueListener?.onSuccessUdp(
                    "Received message successful",
                    Utils.concatDateAndTime(stringData)
                )
            }
        } catch (e: IOException) {
            printLog("Received message failed $e")
        } catch (e: SocketException) {
            printLog(e.message.toString())
        }
    }

    /**
     * Close socket - disconnect socket
     * @param closeSocketListener - closeSocketListener get result of success or failed
     * @see closeSocketListener
     */
    fun closeSocket(closeSocketListener: CloseSocketListener) {
        try {
            if (socket != null || receivedSocket != null) {
                socket?.close()
                socket = null

                receivedSocket?.close()
                receivedSocket = null

                printLog("socket Closed successful")
                closeSocketListener.onSuccess(Utils.concatDateAndTime("Disconnect"))
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