package com.dellainfotech.smartTouch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by Jignesh Dangar on 16-04-2021.
 */

@Parcelize
data class RoomPanelModel(
    var panelType: Int,
    var title: String
) : Parcelable, Serializable