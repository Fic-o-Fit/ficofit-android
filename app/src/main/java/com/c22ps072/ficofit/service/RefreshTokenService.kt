package com.c22ps072.ficofit.service

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.c22ps072.ficofit.data.AuthRepository
import com.c22ps072.ficofit.ui.home.HomeActivity.Companion.START_SERVICE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RefreshTokenService : LifecycleService() {

    @Inject
    lateinit var authRepository: AuthRepository

    private var serviceJob: Job = Job()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.action == START_SERVICE) {
                Log.e("Service", "Service launched")
                lifecycleScope.launchWhenCreated {
                    serviceJob = launch {
                        authRepository.getUserEmail().collect { email ->
                            authRepository.getUserPassword().collect { password ->
                                while(true) {
                                    Log.e("Service", "loop service")
                                    Log.e("Service", "email : $email, password: $password")
                                    authRepository.postUserLogin(email, password).collect { result ->
                                        result.onSuccess { response ->
                                            response.token.let { token ->
                                                authRepository.saveUserToken(token)
                                            }
                                        }
                                    }
                                    delay(300000)
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}