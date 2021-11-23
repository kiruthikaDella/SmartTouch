package com.voinismartiot.voni.api.body

import com.google.gson.annotations.SerializedName

class BodyFeedback(@SerializedName("vFeedback") var feedback: String)