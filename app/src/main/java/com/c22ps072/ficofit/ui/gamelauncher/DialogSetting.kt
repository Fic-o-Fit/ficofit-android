    package com.c22ps072.ficofit.ui.gamelauncher

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.databinding.FragmentDialogSettingBinding

class DialogSetting : DialogFragment() {
    private lateinit var listener: DialogSettingListener
    private lateinit var _binding: FragmentDialogSettingBinding
    private val binding get() = _binding

    interface DialogSettingListener {
        fun onDialogSwitchClick(dialog: DialogFragment)
        fun onDialogModeClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnMode.setOnClickListener {
            listener.onDialogModeClick(this)
        }
        binding.btnSwitchCamera.setOnClickListener {
            listener.onDialogSwitchClick(this)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            builder.setView(inflater.inflate(R.layout.fragment_dialog_setting, null))

                .setNegativeButton("Cancel") { _, _ ->
                    listener.onDialogNegativeClick(this)
                }

            builder.create()
        }  ?: throw IllegalStateException("Activity cannot be null")
    }


}