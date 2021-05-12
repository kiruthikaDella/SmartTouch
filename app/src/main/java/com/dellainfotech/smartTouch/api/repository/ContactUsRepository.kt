package com.dellainfotech.smartTouch.api.repository

import com.dellainfotech.smartTouch.api.SmartTouchApi
import com.dellainfotech.smartTouch.api.body.BodyAddRoom
import com.dellainfotech.smartTouch.api.body.BodyFeedback
import com.dellainfotech.smartTouch.api.body.BodyLogout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactUsRepository @Inject constructor(
    private val smartTouchApi: SmartTouchApi
) : BaseRepository() {

    suspend fun addFeedback(bodyFeedback: BodyFeedback) =
        safeApiCall { smartTouchApi.addFeedback(getAccessKey(), bodyFeedback) }
}