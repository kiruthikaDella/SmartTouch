package com.voinismartiot.voni.mqtt

enum class MQTTConnectionStatus {
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    CONNECTION_LOST,
    DISCONNECTED
}