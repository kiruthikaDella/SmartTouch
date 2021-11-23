package com.voinismartiot.voni.mqtt

import androidx.lifecycle.MutableLiveData
import io.reactivex.subjects.BehaviorSubject

object NotifyManager {

    private val connectionInfo = BehaviorSubject.create<MQTTConnectionStatus>()
    fun getMQTTConnectionInfo() = connectionInfo

    val internetInfo = MutableLiveData<Boolean>()

}