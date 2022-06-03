package com.c22ps072.ficofit.ui.home.bottomsheetdialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.c22ps072.ficofit.databinding.FragmentBottomSheetBinding
import com.c22ps072.ficofit.ui.gamelauncher.CameraActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.icSitUp.setOnClickListener {
            launchCamera()
        }
        binding.icPushUp.setOnClickListener {
            launchCamera()
        }
        binding.icTempleRun.setOnClickListener {
            launchCamera()
        }
    }

    private fun launchCamera() {
        val intent = Intent(activity, CameraActivity::class.java)
        startActivity(intent)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}