package com.c22ps072.ficofit.ui.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c22ps072.ficofit.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
    }
}