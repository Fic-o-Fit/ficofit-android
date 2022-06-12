package com.c22ps072.ficofit.ui.gamelauncher

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DialogSettingListener
        } catch(err: ClassCastException) {
            throw ClassCastException("$context : must implement DialogSettingListener!")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            _binding = FragmentDialogSettingBinding.inflate(inflater)

            builder.setView(binding.root)
                .setNegativeButton("Cancel") { _, _ ->
                    listener.onDialogNegativeClick(this)
                }

            binding.btnSwitchCamera.setOnClickListener {
                listener.onDialogSwitchClick(this)
            }
            binding.btnMode.setOnClickListener {
                listener.onDialogModeClick(this)
            }

            builder.create()
        }  ?: throw IllegalStateException("Activity cannot be null")
    }

}