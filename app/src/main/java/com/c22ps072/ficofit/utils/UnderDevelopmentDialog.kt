package com.c22ps072.ficofit.utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.c22ps072.ficofit.databinding.FragmentDialogUnderDevelopmentBinding

class UnderDevelopmentDialog : DialogFragment() {
    private lateinit var _binding: FragmentDialogUnderDevelopmentBinding
    private val binding get() = _binding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            _binding = FragmentDialogUnderDevelopmentBinding.inflate(inflater)

            builder.setView(binding.root)

            binding.btnDismiss.setOnClickListener {
                dismiss()
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}