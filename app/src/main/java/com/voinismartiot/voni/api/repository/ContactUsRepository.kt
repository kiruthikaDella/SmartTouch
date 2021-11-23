package com.voinismartiot.voni.api.repository

import com.voinismartiot.voni.api.SmartTouchApi
import com.voinismartiot.voni.api.body.BodyFeedback
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactUsRepository @Inject constructor(
    private val smartTouchApi: SmartTouchApi
) : BaseRepository() {

    suspend fun addFeedback(bodyFeedback: BodyFeedback) =
        safeApiCall { smartTouchApi.addFeedback(getAccessKey(), bodyFeedback) }
}