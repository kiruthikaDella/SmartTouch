package com.dellainfotech.smartTouch

import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.mqtt.AwsMqttSingleton
import com.teksun.tcpudplibrary.SettingsService

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class AppDelegate: Application(), LifecycleObserver {

    companion object {
        lateinit var instance: AppDelegate
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        //Initialize FastSave
        FastSave.init(this)

        AwsMqttSingleton.initializeMQTT()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        SettingsService.init(this)
    }
}