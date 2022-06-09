package com.c22ps072.ficofit.service

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.c22ps072.ficofit.ui.home.HomeActivity.Companion.START_SERVICE
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RefreshTokenService : LifecycleService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.action == START_SERVICE) {
                Log.e("Service", "Service launched")
                lifecycleScope.launchWhenCreated {
                    launch {
                        while(true) {
                            delay(5000)
                            Log.e("Service", "loop service")
                        }
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}