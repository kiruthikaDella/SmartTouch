package com.dellainfotech.smartTouch.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */

@Parcelize
data class SwitchIconsModel(
    var switchName: String,
    var switchNumber: String
) : Parcelable, Serializable