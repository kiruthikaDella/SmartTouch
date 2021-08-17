package com.dellainfotech.smartTouch.common.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.AppDelegate
import com.facebook.appevents.internal.AppEventUtility.bytesToHex
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Created by Jignesh Dangar on 13-04-2021.
 */
object Utils {

    fun isInternetAvailable(): Boolean {
        return try {
            val timeoutMs = 1500
            val sock = Socket()
            val sockAddress: SocketAddress = InetSocketAddress("8.8.8.8", 53)
            sock.connect(sockAddress, timeoutMs)
            sock.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isNetworkConnectivityAvailable(): Boolean {
        var isConnected = false
        val cm =
            AppDelegate.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = cm.activeNetwork ?: return false
            val activeNetwork = cm.getNetworkCapabilities(networkCapabilities) ?: return false
            isConnected = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            cm.run {
                activeNetworkInfo?.run {
                    isConnected = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }

        return isConnected
    }

    fun generateSSHKey(context: Context) {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("AppLog", "key:$hashKey=")
            }
        } catch (e: Exception) {
            Log.e("AppLog", "error:", e)
        }

    }

    fun getToken(nonce: String, time: String): String {
        val data = Constants.SECRET_KEY + time + nonce
        return encode(Constants.PRIVATE_KEY, data)
    }

    @Throws(java.lang.Exception::class)
    fun encode(key: String, data: String): String {
        val sha256Hmac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(key.toByteArray(charset("UTF-8")), "HmacSHA256")
        sha256Hmac.init(secretKey)
        return bytesToHex(sha256Hmac.doFinal(data.toByteArray(charset("UTF-8"))))
    }

    fun getTimeZone(): Long {
        return System.currentTimeMillis() / 1000
    }

    fun nonce(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        val salt = StringBuilder()
        val rnd = Random()
        while (salt.length < 6) { // length of the random string.
            val index = (rnd.nextFloat() * chars.length).toInt()
            salt.append(chars[index])
        }
        return salt.toString()
    }

    fun isMasterUser(): Boolean {
        return FastSave.getInstance().getBoolean(Constants.IS_MASTER_USER, false)
    }

    fun isControlModePin(): Boolean {
        return FastSave.getInstance().getBoolean(Constants.isControlModePinned, Constants.DEFAULT_CONTROL_MODE_STATUS)
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    fun Int.toBoolean(): Boolean = this == 1 // return true if value is 1, else return false

    fun Boolean.toInt(): Int = if (this) 1 else 0 // return 1 if true, else return 0

    fun Boolean.toReverseInt(): Int = if (this) 0 else 1 // return 0 if true, else return 1

    fun Int.toReverseInt(): Int = if (this == 1) 0 else 1 // return 0 if 1, else return 1

    fun EditText.clearError() {
        error = null
    }

}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.hideSoftKeyboard() {
    currentFocus?.let {
        val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}