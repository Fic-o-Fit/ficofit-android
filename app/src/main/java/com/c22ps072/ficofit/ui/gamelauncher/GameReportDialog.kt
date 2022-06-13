package com.c22ps072.ficofit.ui.gamelauncher

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.data.source.local.PreferenceDataStore.Companion.USER_CALORIES_BURN
import com.c22ps072.ficofit.databinding.FragmentDialogGameReportBinding
import com.c22ps072.ficofit.di.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class GameReportDialog : DialogFragment() {
    private lateinit var listener: ReportDialogListener
    private lateinit var _binding: FragmentDialogGameReportBinding
    private val binding get() = _binding

    interface ReportDialogListener {
        fun onButtonCloseListener(dialog: GameReportDialog, calories: Double)
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
            val calories = args?.getString(TEXT_CALORIES)
            var totalCalories = 0.0

            binding.tvPointsEarned.text = "+${points}pts"

            if (calories != null) {
                binding.tvCaloriesBurn.text =
                    getString(R.string.s_calories_burn, calories.toString())

                val currentCalories = runBlocking {
                    requireContext().dataStore.data.first()[USER_CALORIES_BURN]
                }
                totalCalories = (currentCalories ?: 0.0) + calories.toDouble()

            } else {
                binding.tvCaloriesBurn.visibility = View.GONE
            }

            binding.btnOk.setOnClickListener {
                listener.onButtonCloseListener(this, totalCalories)
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        const val TEXT_POINT = "text_point"
        const val TEXT_CALORIES = "text_calories"
    }
}