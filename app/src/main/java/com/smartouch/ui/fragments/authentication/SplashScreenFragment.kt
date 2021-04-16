package com.smartouch.ui.fragments.authentication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.smartouch.R

/**
 * Created by Jignesh Dangar on 09-04-2021.
 */

class SplashScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToLoginFragment())
        }, 2000)
    }
}