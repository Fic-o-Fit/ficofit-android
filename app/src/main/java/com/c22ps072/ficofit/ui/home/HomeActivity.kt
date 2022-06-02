package com.c22ps072.ficofit.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.databinding.ActivityHomeBinding
import com.c22ps072.ficofit.ui.home.bottomsheetdialog.BottomSheet
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigationView

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        navView.menu.getItem(1).isEnabled = false

        binding.btnGame.setOnClickListener {
            supportFragmentManager.commit {
                add(BottomSheet(), BottomSheet::class.java.simpleName)
            }
        }
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}