package com.voinismartiot.voni.common.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appizona.yehiahd.fastsave.FastSave
import com.facebook.appevents.internal.AppEventUtility.bytesToHex
import com.google.firebase.iid.FirebaseInstanceId
import com.voinismartiot.voni.AppDelegate
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Utils {

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
        return FastSave.getInstance()
            .getBoolean(Constants.isControlModePinned, Constants.DEFAULT_CONTROL_MODE_STATUS)
    }

    fun getImageUri(inContext: Context, inImage: Bitmap, imageName: String): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, imageName, null)
        return Uri.parse(path)
    }

    fun clearFirebaseToken(){
        Thread {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    fun Int.toBoolean(): Boolean = this == 1 // return true if value is 1, else return false

    fun String.toBoolean(): Boolean = this == "1" // return true if value is 1, else return false

    fun Boolean.toInt(): Int = if (this) 1 else 0 // return 1 if true, else return 0

    fun Boolean.toReverseInt(): Int = if (this) 0 else 1 // return 0 if true, else return 1

    fun Int.toReverseInt(): Int = if (this == 1) 0 else 1 // return 0 if 1, else return 1

    fun EditText.clearError() {
        error = null
    }

    fun getFCMToken(): String {
        return FastSave.getInstance().getString(Constants.FCM_TOKEN, "")
    }

    fun String.isSmartouch(): Boolean = this.lowercase() == Constants.PRODUCT_SMART_TOUCH

    fun String.isSmartAck(): Boolean = this.lowercase() == Constants.PRODUCT_SMART_ACK

    fun String.isSmartAp(): Boolean = this.lowercase() == Constants.PRODUCT_SMART_AP

    fun String.isValidEmail(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun String.stringToFloat(): Float {
        return try {
            this.toFloat()
        }catch (e: Exception){
            e.printStackTrace()
            0.0.toFloat()
        }
    }

    fun String.stringToInt(): Int{
        return try {
            this.toInt()
        }catch (e: Exception){
            e.printStackTrace()
            0
        }
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
        val inputMethodManager =
            ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Context.showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

