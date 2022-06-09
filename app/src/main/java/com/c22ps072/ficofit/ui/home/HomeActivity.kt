package com.c22ps072.ficofit.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.databinding.ActivityHomeBinding
import com.c22ps072.ficofit.service.RefreshTokenService
import com.c22ps072.ficofit.ui.authentication.AuthenticationActivity
import com.c22ps072.ficofit.ui.authentication.AuthenticationViewModel
import com.c22ps072.ficofit.ui.home.bottomsheetdialog.BottomSheet
import com.c22ps072.ficofit.ui.home.setting.LogoutDialog
import com.c22ps072.ficofit.ui.home.setting.SettingsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), LogoutDialog.LogoutDialogListener {

    private lateinit var binding: ActivityHomeBinding
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentService = Intent(this, RefreshTokenService::class.java)
        intentService.action = START_SERVICE
        startService(intentService)

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

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        dialog.dismiss()
        lifecycleScope.launch {
            val intent = Intent(this@HomeActivity, AuthenticationActivity::class.java)
            startActivity(intent)
            finish()
            settingsViewModel.logout()
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
        const val START_SERVICE = "start_service"
    }
}