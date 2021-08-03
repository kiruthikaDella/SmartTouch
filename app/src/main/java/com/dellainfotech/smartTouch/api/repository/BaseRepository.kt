package com.dellainfotech.smartTouch.api.repository

import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.api.Resource
import com.dellainfotech.smartTouch.common.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Singleton

@Singleton
abstract class BaseRepository {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        Resource.Failure(false, e.code(), e.response()?.errorBody())
                    }
                    else -> {
                        Resource.Failure(true, null, null)
                    }
                }
            }
        }
    }

    protected fun getAccessKey(): String {
        return FastSave.getInstance().getString(Constants.ACCESS_TOKEN, null)
    }
}