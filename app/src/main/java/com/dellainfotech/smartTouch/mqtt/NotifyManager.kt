package com.dellainfotech.smartTouch.mqtt

import io.reactivex.subjects.BehaviorSubject

object NotifyManager {

    private val connectionInfo = BehaviorSubject.create<MQTTConnectionStatus>()
    fun getMQTTConnectionInfo() = connectionInfo

}