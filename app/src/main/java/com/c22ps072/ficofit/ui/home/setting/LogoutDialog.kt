package com.c22ps072.ficofit.ui.home.setting

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class LogoutDialog : DialogFragment() {
    private lateinit var listener: LogoutDialogListener

    interface LogoutDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setMessage("Are you sure want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton("Cancel") { _, _ ->
                    listener.onDialogNegativeClick(this)
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as LogoutDialogListener
        } catch(err: ClassCastException) {
            throw ClassCastException("$context : must implement LogoutDialogListener!")
        }
    }
}