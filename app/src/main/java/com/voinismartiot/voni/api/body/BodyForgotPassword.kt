package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyForgotPassword(@SerializedName("vEmail") var userEmail: String)