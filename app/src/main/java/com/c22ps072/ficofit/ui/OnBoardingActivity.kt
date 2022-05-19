package com.c22ps072.ficofit.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.c22ps072.ficofit.MainActivity
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabIntent.setOnClickListener {
            Log.e("OnBoarding", "FAB clicked")
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}