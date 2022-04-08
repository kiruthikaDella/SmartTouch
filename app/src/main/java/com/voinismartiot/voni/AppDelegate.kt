package com.voinismartiot.voni

import android.app.Application
import com.appizona.yehiahd.fastsave.FastSave
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.FirebaseApp
import com.teksun.tcpudplibrary.SettingsService
import com.voinismartiot.voni.mqtt.AwsMqttSingleton

class AppDelegate : Application() {

    companion object {
        lateinit var instance: AppDelegate
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        //Initialize FastSave
        FastSave.init(this)

        FirebaseApp.initializeApp(this)
        AwsMqttSingleton.initializeMQTT()

        SettingsService.init(this)
    }
}