package com.smartouch.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by Jignesh Dangar on 27-04-2021.
 */

@Parcelize
data class SwitchIconsDetailModel(
    @DrawableRes
    var image: Int,
    var title: String
) : Parcelable, Serializable