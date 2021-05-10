package com.dellainfotech.smartTouch.mqtt

enum class MQTTConnectionStatus {
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    CONNECTION_LOST,
    DISCONNECTED
}