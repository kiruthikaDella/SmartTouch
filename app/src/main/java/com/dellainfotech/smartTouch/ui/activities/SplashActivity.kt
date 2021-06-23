package com.dellainfotech.smartTouch.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.appizona.yehiahd.fastsave.FastSave
import com.dellainfotech.smartTouch.R
import com.dellainfotech.smartTouch.common.utils.Constants

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window?.statusBarColor = getColor(R.color.white)
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({

            if (FastSave.getInstance().getBoolean(Constants.IS_LOGGED_IN, false)
            ) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, AuthenticationActivity::class.java))
            }
        }, 2000)
    }
}