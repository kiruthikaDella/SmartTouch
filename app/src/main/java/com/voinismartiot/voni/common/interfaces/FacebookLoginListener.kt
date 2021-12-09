package com.voinismartiot.voni.common.interfaces

interface FacebookLoginListener {
    fun performLogin(userId: String, email: String)
}