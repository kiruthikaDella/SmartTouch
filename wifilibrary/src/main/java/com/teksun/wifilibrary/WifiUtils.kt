package com.teksun.wifilibrary

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.*
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.teksun.wifilibrary.listener.*


@SuppressLint("StaticFieldLeak")
object WifiUtils {
    private val logTag = WifiUtils::class.java.simpleName
    private var mContext: Context? = null
    private var instance: WifiUtils? = null

    private var mWifiManager: WifiManager? = null
    var mConnectivityManager: ConnectivityManager? = null
    var mNetworkCallback: ConnectivityManager.NetworkCallback? = null

    private var receiverWifi: WifiReceiver? = null

    private var isEnableLog = false

    /**
     * Initialize context and Wifi Manager
     * @param context
     */
    fun init(context: Context?) {
        mContext = context

        mWifiManager =
            context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    /**
     * Enable / Disable print Log
     * @param boolean - true than enable else disable
     */
    fun enableLog(boolean: Boolean) {
        isEnableLog = boolean
    }

    /**
     * return Instance
     */
    fun getInstance(): WifiUtils {
        if (instance == null) {
            validateInitialization()
            synchronized(WifiUtils::class.java) { instance = this }
        }
        return instance!!
    }

    /**
     * return wifi manager
     */
    fun getWifiManager(): WifiManager? {
        return mWifiManager
    }

    /**
     * Enable wifi programmatically
     */
    fun enableWifi() {
        if (VersionUtils.isAndroidQOrLater) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            (mContext as Activity).startActivityForResult(panelIntent, 0)
        } else {
            mWifiManager?.apply { isWifiEnabled = true }
        }

    }

    /**
     * Disable wifi programmatically
     */
    fun disableWifi() {
        if (VersionUtils.isAndroidQOrLater) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            (mContext as Activity).startActivityForResult(panelIntent, 0)
        } else {
            mWifiManager?.apply { isWifiEnabled = false }
        }
    }

    /**
     * Get wifi scan result list
     * @param scanResult - return scan result list using ScanResultListener
     * @see ScanResultListener
     *
     * @RequiresPermission(allOf = [permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION, permission.ACCESS_WIFI_STATE])
     * Require Location is enable starting from android 6
     * You can enable location service using this code
     * startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
     */
    fun getScanResult(scanResult: ScanResultListener) {
        if (mWifiManager == null) {
            scanResult.onFailure("Could not get wifi manager")
            return
        }

        if (mWifiManager?.isWifiEnabled!!) {
            var scanFlag = true
            mContext?.let {
                if (VersionUtils.isAndroidQOrLater) {
                    if (ActivityCompat.checkSelfPermission(
                            it,
                            permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        scanResult.onFailure("Location permission not granted")
                        scanFlag = false
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(
                            it,
                            permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                            it,
                            permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        scanResult.onFailure("Location permission not granted")
                        scanFlag = false
                    }
                }

                if (VersionUtils.isAndroidMOrLater) {
                    if (!isLocationEnabled()) {
                        scanFlag = false
                        printLog("Location service not enable")
                        scanResult.onFailure("Location service not enable")
                        mContext?.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                }
            }

            if (scanFlag) {
                receiverWifi = WifiReceiver(scanResult)
                mContext?.let {
                    it.registerReceiver(
                        receiverWifi,
                        IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                    )
                    mWifiManager?.startScan()
                }
/*
                val list = mWifiManager?.scanResults!!
                scanResult.onScanResult(list)
*/
            }
        } else {
            scanResult.onFailure("Wifi is not enable")
        }
    }

    private fun isLocationEnabled(): Boolean {
        val le = Context.LOCATION_SERVICE
        val locationManager = mContext?.getSystemService(le) as LocationManager?
        return locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)!!
    }

    /**
     * Connect wifi
     * Note: Connected till app is not closed
     * @param ssid - ssid / name of wifi
     * @param password - password for connect wifi
     * @param connectResultListener - return result of connection is success or failed
     * @see ConnectResultListener
     *
     * @RequiresPermission(allOf = [permission.CHANGE_NETWORK_STATE, permission.ACCESS_WIFI_STATE, permission.ACCESS_FINE_LOCATION])
     */
    fun connectWith(ssid: String, password: String, connectResultListener: ConnectResultListener) {

        if (mWifiManager == null) {
            connectResultListener.onFailure("Could not get wifi manager")
            return
        }

        if (VersionUtils.isAndroidQOrLater) {
            if (!mWifiManager?.isWifiEnabled!!) {
                printLog("Please turn on wifi")
                connectResultListener.onFailure("Please turn on wifi")
            }
            val wifiNetworkSpecifier =
                WifiNetworkSpecifier.Builder()
                    .setSsid(ssid)
                    .setWpa2Passphrase(password)
                    .build()

            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(wifiNetworkSpecifier)
                .build()

            mConnectivityManager =
                mContext?.applicationContext?.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager

            mNetworkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    createNetworkRoute(network)
                    printLog("Connection Successful")
                    connectResultListener.onSuccess("Connection successful")
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    printLog("Connection Failed")
                    connectResultListener.onFailure("Connection failed")
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    printLog("Connection Failed")
                    connectResultListener.onFailure("Connection failed")
                }
            }

            mConnectivityManager?.requestNetwork(networkRequest, mNetworkCallback!!)

        } else {
            if (!mWifiManager?.isWifiEnabled!!) {
                mWifiManager?.isWifiEnabled = true
            }

            val wifiConfig = WifiConfiguration()
            wifiConfig.SSID = "\"" + ssid + "\"";
            val networkList: List<ScanResult>? = mWifiManager?.scanResults!!
            if (networkList != null) {
                val list = networkList.filter { it.SSID == ssid }
                for (network in list) {
                    if (network.SSID.contains(ssid)) {
                        val capabilities = network.capabilities
                        printLog(network.SSID + " capabilities : " + capabilities)

                        //Then you could add some code to check for a specific security type.
                        when {
                            capabilities.contains("WPA") -> {
                                printLog("contains WPA")

                                wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                                wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                                wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                                wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                                wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                                wifiConfig.preSharedKey = "\"" + password.toString() + "\""
                                wifiConfig.status = WifiConfiguration.Status.ENABLED
                            }
                            capabilities.contains("WEP") -> {
                                // We know there is WEP encryption
                                printLog("contains WEP")

                                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                                wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                                wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                                wifiConfig.wepKeys[0] = password
                            }
                            else -> {
                                // Another type of security scheme, open wifi, captive portal, etc..
                                printLog("contains open")
                                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                            }
                        }
                        var netId: Int = mWifiManager?.addNetwork(wifiConfig)!!
                        if (netId == -1) {
                            // Get existed network id if it is already added to WiFi network
                            netId = getExistingNetworkId(ssid)
                            printLog("getExistingNetworkId: $netId")
                        }
                        val isDisconnect = mWifiManager?.disconnect()
                        printLog("contains disconnect $isDisconnect")

                        val enabled: Boolean = mWifiManager?.enableNetwork(netId, true)!!
                        printLog("contains enabled $enabled")

                        if (enabled) {
                            val isConnectionSuccessful = mWifiManager?.reconnect()!!

                            if (isConnectionSuccessful) {
                                printLog("Connection Successful")
                                connectResultListener.onSuccess("Connection successful")
                            } else {
                                printLog("Connection Failed")
                                connectResultListener.onFailure("Connection failed")
                            }
                        } else {
                            printLog("Can't enable network")
                            connectResultListener.onFailure("Connection failed")
                        }
                        break
                    } else {
                        printLog("SSID not match")
                        connectResultListener.onFailure("SSID not match")
                    }
                }
            }
        }
    }

    /**
     *  @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.ACCESS_FINE_LOCATION])
     */
    private fun getExistingNetworkId(SSID: String): Int {
        val configuredNetworks = mWifiManager?.configuredNetworks
        if (configuredNetworks != null) {
            for (existingConfig in configuredNetworks) {
                if (existingConfig.SSID == SSID) {
                    return existingConfig.networkId
                }
            }
        }
        return -1
    }

    /**
     * For access internet in application
     * @param network
     */
    private fun createNetworkRoute(network: Network) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mConnectivityManager?.bindProcessToNetwork(network);
        } else {
            ConnectivityManager.setProcessDefaultNetwork(network);
        }

    }

    /**
     * Disconnect wifi not remove in save configure list
     * disconnect from the currently connected network.
     * @param ssid - ssid / name of wifi
     * @param disconnectionListener - return result of disconnected wifi is success or failed
     * @see DisconnectResultListener
     *
     * @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_WIFI_STATE])
     */
    fun disconnect(ssid: String, disconnectionListener: DisconnectResultListener) {
        if (VersionUtils.isAndroidQOrLater) {
            if (mConnectivityManager == null) {
                disconnectionListener.onFailure("Could not get connectivity manager")
                return
            }
            mConnectivityManager?.unregisterNetworkCallback(mNetworkCallback!!)
            disconnectionListener.onSuccess("Disconnect Successfully")
        } else {
            if (mWifiManager == null) {
                disconnectionListener.onFailure("Could not get wifi manager")
                return
            }
            if (disconnectWifi(ssid)) {
                disconnectionListener.onSuccess("Disconnect Successfully")
            } else {
                disconnectionListener.onSuccess("Could not Disconnect")
            }
        }
    }

    /**
     * @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_WIFI_STATE])
     */
    private fun disconnectWifi(ssid: String): Boolean {
        val isDisconnect = mWifiManager?.disconnect()!!
        if (isDisconnect) {
            val wifiConfig = getWifiConfiguration(ssid)
            wifiConfig?.networkId?.let {
                return mWifiManager?.removeNetwork(it)!!
            }
            return false
        } else {
            return false
        }

    }

    /**
     * add configure wifi
     * It's work on up to Android 9 [Api level - 28]
     * It's deprecated after 9
     *
     * @param ssid - ssid / name of wifi
     * @param password - password for save wifi i network
     * @param savedNetworkResultListener - return result of connection is success or failed
     * @see SavedNetworkResultListener
     */
    fun addNetwork(
        SSID: String,
        password: String,
        keyMgmt: Int = WifiConfiguration.KeyMgmt.NONE,
        savedNetworkResultListener: SavedNetworkResultListener
    ) {
        if (VersionUtils.isAndroidPOrEarly) {
            val conf = WifiConfiguration()
            conf.SSID = "\"$SSID\""
            conf.preSharedKey = if (password.isNullOrEmpty()) "" else "\"$password\""
            conf.allowedKeyManagement.set(keyMgmt)
            when (keyMgmt) {
                WifiConfiguration.KeyMgmt.WPA_PSK -> {
                    //WPA/WPA2
                }
                WifiConfiguration.KeyMgmt.IEEE8021X -> {
                }
                WifiConfiguration.KeyMgmt.WPA_EAP -> {
                }
                WifiConfiguration.KeyMgmt.NONE -> {
                    if (password.isNullOrEmpty()) {
                        //open network
                        conf.wepKeys[0] = "\"\""
                    } else {
                        //wep
                        conf.wepKeys[0] = "\"" + password + "\""
                        conf.wepTxKeyIndex = 0
                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                    }
                }
            }
            if (password.isNullOrEmpty()) {
                //open network
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            } else {
            }
            mWifiManager?.isWifiEnabled = true
            while (!mWifiManager?.isWifiEnabled()!!) {
                Log.d("AppLog", "waiting to be able to add network")
            }
            val networkId = mWifiManager?.addNetwork(conf)
            if (networkId == -1) {
                Log.d("AppLog", "failed to add network")
                savedNetworkResultListener.onFailure("Save Network Failed : Network id is -1")
            } else {
                mWifiManager?.enableNetwork(networkId!!, false)
                Log.d("AppLog", "success to add network")
                savedNetworkResultListener.onSuccess("Save Network Successfully", networkId)
            }
        } else if (VersionUtils.isAndroidQ) {
            val list = ArrayList<WifiNetworkSuggestion>()
            val builder = WifiNetworkSuggestion.Builder().setSsid(SSID)
            if (!password.isNullOrEmpty())
                builder.setWpa2Passphrase(password)
            list.add(builder.build())
            val result = mWifiManager?.addNetworkSuggestions(list)
            if (result == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS)
                savedNetworkResultListener.onSuccess(message = "Success")
            else savedNetworkResultListener.onFailure("Failed")
        } else if (VersionUtils.isAndroidROrLater) {

            val suggestion1 = WifiNetworkSuggestion.Builder()
                .setSsid(SSID)
                .setWpa2Passphrase(password)
                .build()
            savedNetworkResultListener.onSuccess(suggestionList = arrayListOf(suggestion1))
        }
    }

    /**
     * Remove and saved network configuration wifi
     * remove the saved wifi network configuration. On Android 10, this will just simply disconnect
     * It's work on up tp Android 9 [Api level - 28]
     * It's deprecated after 9
     *
     * @param ssid - ssid / name of wifi
     * @param removeNetworkResultListener - return result of remove network is success or failed
     * @see RemoveNetworkResultListener
     *
     * @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_WIFI_STATE])
     */
    fun removeNetwork(ssid: String, removeNetworkResultListener: RemoveNetworkResultListener) {
        if (VersionUtils.isAndroidPOrEarly) {
            val wifiConfig = getWifiConfiguration(ssid)
            val isRemove = mWifiManager?.removeNetwork(wifiConfig?.networkId!!)!!

            if (isRemove) {
                removeNetworkResultListener.onSuccess("Remove network ${wifiConfig?.networkId!!} successfully")
            } else {
                removeNetworkResultListener.onFailure("Remove network ${wifiConfig?.networkId!!} failed")
            }
        } /*else if (VersionUtils.isAndroidQOrLater) {
            val suggestion = WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
//                .setWpa2Passphrase(password)
                .build()

            val list = arrayListOf<WifiNetworkSuggestion>()
            list.add(suggestion)
            mWifiManager?.removeNetworkSuggestions(suggestion)
        }*/
    }

    /**
     * @return get connected wifi ssid
     * If location permission is not granted than it's shown unknown ssid
     *
     * @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
     */
    fun getCurrentSSID(): String? {

        val info = mWifiManager?.connectionInfo!!
        return if (info.supplicantState == SupplicantState.COMPLETED) {
            Log.i(logTag, info.ssid)
            info.ssid.replace("\"", "")
        } else {
            null
        }
    }

    /**
     * get gateway ip address
     */
    fun getGatewayIpAddress(): String {
        val dhcpWifiInfo = mWifiManager?.dhcpInfo
        return intToIp(dhcpWifiInfo?.gateway!!)
    }

    private fun intToIp(addr: Int): String {
        var address = addr
        return (address and 0xFF).toString() + "." +
                (8.let { address = address ushr it; address } and 0xFF) + "." +
                (8.let { address = address ushr it; address } and 0xFF) + "." +
                (8.let { address = address ushr it; address } and 0xFF)
    }

    /**
     * get configure wifi list
     * get list in api >= 9
     * @param ConfigureWifiResultListener - return result of configure list is success or failed
     * @see ConfigureWifiResultListener
     */
    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.ACCESS_FINE_LOCATION])
    fun getConfigureWifiList(ConfigureWifiResultListener: ConfigureWifiResultListener) {
        mContext?.let {
            if (ActivityCompat.checkSelfPermission(
                    it,
                    permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ConfigureWifiResultListener.onFailure("Location permission not granted")
            } else {
                val configureList = mWifiManager?.configuredNetworks
                if (!configureList.isNullOrEmpty()) {
                    ConfigureWifiResultListener.onConfigureResult(configureList)
                } else {
                    ConfigureWifiResultListener.onFailure("configureList is empty")
                }
            }
        }
    }

    /**
     * Get information about currently connected wifi
     * @param wifiInfoResultListener - return result of connected wifi information is success or failed
     * @see WifiInfoResultListener
     */
    fun getConnectedWifiInfo(wifiInfoResultListener: WifiInfoResultListener) {
        if (mWifiManager == null) {
            printLog("Could not get wifi manager")
        } else {
            val wInfo = mWifiManager?.connectionInfo
            wInfo?.let {
                wifiInfoResultListener.onWifiInfoResult(it)
            }
        }

    }

    /**
     * @param wifiSSID - is connected or not
     * @return boolean
     */
    fun isSSIDWifiConnected(wifiSSID: String): Boolean {
        if (mWifiManager == null) {
            printLog("Could not get wifi manager")
        } else {
            val info = mWifiManager?.connectionInfo!!
            return info.ssid.contains(wifiSSID)
        }
        return false
    }

    /**
     * Get wifi configuration of given param ssid
     * @param ssid - ssid / name of wifi
     */
    @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_WIFI_STATE])
    fun getWifiConfiguration(ssid: String): WifiConfiguration? {
        if (mWifiManager == null) {
            printLog("Could not get wifi manager")
        } else {

            val configuredNetworks = mWifiManager?.configuredNetworks
            val findSSID = '"'.toString() + ssid + '"'
            configuredNetworks?.let {
                for (wifiConfiguration in configuredNetworks) {
                    if (wifiConfiguration.SSID == findSSID) {
                        return wifiConfiguration
                    }
                }
            }
        }
        return null
    }

    /**
     * Validate Wifi library is initialize
     */
    private fun validateInitialization() {
        if (mWifiManager == null)
            throw RuntimeException("Wifi library must be initialize inside your application by calling WifiUtils.init(this)")
    }

    /**
     * Print log is log is enable
     * @param message - print message in log
     */
    private fun printLog(message: String) {
        if (isEnableLog) Log.i(logTag, message)
    }

    /**
     * Unregister receiver
     */
    fun unregisterScanReceiver() {
        if (receiverWifi != null)
            mContext?.unregisterReceiver(receiverWifi)
    }

    /**
     * Broadcast receiver class called its receive method
     * when number of wifi connections changed
     */
    private class WifiReceiver(private var scanResult: ScanResultListener) : BroadcastReceiver() {
        // This method call when number of wifi connections changed
        override fun onReceive(context: Context?, intent: Intent?) {
            printLog("OnReceive called")
            val list = mWifiManager?.scanResults!!
            if (!list.isNullOrEmpty()) scanResult.onScanResult(list)
        }
    }
}



