package com.example.notepad.ReminderDialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.notepad.R
import com.example.notepad.edittext.EditTextFragment

class ReminderDialogFragment(val listener : ReminderDialogListener) : DialogFragment() {

    interface ReminderDialogListener {
        fun onDialogPositiveClick( dateTime: DateTime)
        fun onDialogNegativeClick(dateTime: DateTime)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val dateTime = DateTime()


            builder.setView(inflater.inflate(R.layout.fragment_reminder,null))
                .setMessage(R.string.set_reminder)
                .setPositiveButton(R.string.save,
                    DialogInterface.OnClickListener { dialog, id ->
                        dateTime.isSet=true
                        listener.onDialogPositiveClick(dateTime)
                        dialog.dismiss()
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                        listener.onDialogNegativeClick(dateTime)
                        dialog.dismiss()
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}


