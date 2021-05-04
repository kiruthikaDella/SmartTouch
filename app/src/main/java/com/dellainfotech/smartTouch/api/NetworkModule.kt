package com.dellainfotech.smartTouch.api

import android.util.Log
import com.dellainfotech.smartTouch.common.utils.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient.Builder
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    private
    var logging: HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    // set your desired log level
    private val httpClient: Builder = Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .addInterceptor(providesInterceptor())
        .addInterceptor(logging)

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(SmartTouchApi.BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    @Provides
    @Singleton
    fun provideSmartTouchApi(retrofit: Retrofit): SmartTouchApi =
        retrofit.create(SmartTouchApi::class.java)

    @Provides
    fun providesInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()

            val nonce = Utils.nonce()
            val timestamp = Utils.getTimeZone().toString()
            val token = Utils.getToken(nonce, timestamp)

            // Customize the request
            val request = original.newBuilder()
                .header("Accept", "application/json")
                .header("nonce", nonce)
                .header("timestamp", timestamp)
                .header("token", token)
                .build()
            val response = chain.proceed(request)
            response
        }
    }

}
