package com.c22ps072.ficofit.ui.gamelauncher

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.c22ps072.ficofit.databinding.FragmentDialogGameReportBinding

class GameReportDialog : DialogFragment() {
    private lateinit var listener: ReportDialogListener
    private lateinit var _binding: FragmentDialogGameReportBinding
    private val binding get() = _binding

    interface ReportDialogListener {
        fun onButtonCloseListener(dialog: GameReportDialog)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ReportDialogListener
        } catch (err: ClassCastException) {
            throw ClassCastException("$context : must implement DialogSettingListener!")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            _binding = FragmentDialogGameReportBinding.inflate(inflater)

            builder.setView(binding.root)

            val args: Bundle? = arguments
            val points = args?.getString(TEXT_POINT)

            binding.tvPointsEarned.text = "+${points}pts"

            binding.btnOk.setOnClickListener {
                listener.onButtonCloseListener(this)
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        const val TEXT_POINT = "text_point"
    }
}