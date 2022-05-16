package com.voinismartiot.voni.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import com.appizona.yehiahd.fastsave.FastSave
import com.voinismartiot.voni.R
import com.voinismartiot.voni.common.utils.Constants
import com.voinismartiot.voni.common.utils.hideDialog
import com.voinismartiot.voni.common.utils.hideSoftKeyboard

class SplashActivity : AppCompatActivity() {
    private lateinit var content: View
    private var isHandlerSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = findViewById(android.R.id.content)
        setContentView(R.layout.activity_splash)
        window?.statusBarColor = getColor(R.color.white)
    }

    override fun onResume() {
        super.onResume()

        hideSoftKeyboard()

        Handler(Looper.getMainLooper()).postDelayed({
            isHandlerSet = true
        }, 2000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            content.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return when {
                        isHandlerSet -> {
                            content.viewTreeObserver.removeOnPreDrawListener(this)
                            startAnotherActivity()
                            true
                        }
                        else -> false
                    }
                }
            })
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                startAnotherActivity()
            }, 2000)
        }
    }

    private fun startAnotherActivity() {
        if (FastSave.getInstance().getBoolean(Constants.IS_LOGGED_IN, false)) {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        } else {
            startActivity(Intent(this@SplashActivity, AuthenticationActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        hideSoftKeyboard()
    }

    override fun onDestroy() {
        hideDialog()
        super.onDestroy()
    }
}