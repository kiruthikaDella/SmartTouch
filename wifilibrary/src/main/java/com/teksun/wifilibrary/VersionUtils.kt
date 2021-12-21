package com.teksun.wifilibrary

import android.os.Build

object VersionUtils {
    val isAndroidROrLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    val isAndroidQOrLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    val isAndroidPOrEarly: Boolean
        get() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.P

    val isAndroidMOrLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    val isAndroidQ: Boolean
        get() = Build.VERSION.SDK_INT == Build.VERSION_CODES.Q

}