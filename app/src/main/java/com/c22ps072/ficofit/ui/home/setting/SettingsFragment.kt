package com.c22ps072.ficofit.ui.home.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.c22ps072.ficofit.BuildConfig
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ficofitVersion = "${getString(R.string.app_name)} v${BuildConfig.VERSION_NAME}"
        binding.textView6.text = ficofitVersion

        // Share and Feedback features is Under Development in this (alpha) version
        binding.btnSendFeedback.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_setting_to_underDevelopmentDialog)
        }
        binding.btnShareApp.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_setting_to_underDevelopmentDialog)
        }

        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_setting_to_logoutDialog)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}