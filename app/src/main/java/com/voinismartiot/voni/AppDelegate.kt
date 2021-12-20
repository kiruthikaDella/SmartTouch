package com.voinismartiot.voni

import android.app.Application
import com.appizona.yehiahd.fastsave.FastSave
import com.google.firebase.FirebaseApp
import com.teksun.tcpudplibrary.SettingsService
import com.voinismartiot.voni.mqtt.AwsMqttSingleton

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

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