package com.example.notepad.ReminderDialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.notepad.R
import com.example.notepad.databinding.ActivityMainBinding.inflate
import kotlinx.coroutines.NonCancellable.cancel

class ReminderDialogFragment : DialogFragment() {

    // Use this instance of the interface to deliver action events
    internal lateinit var listener: ReminderDialogListener

    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    interface ReminderDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, dateTime: DateTime)
        fun onDialogNegativeClick(dialog: DialogFragment, dateTime: DateTime)
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as ReminderDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val dateTime = DateTime()


            builder.setView(inflater.inflate(R.layout.fragment_reminder,null))
                .setPositiveButton(R.string.save,
                    DialogInterface.OnClickListener { dialog, id ->
                        dateTime.isSet=true
                        listener.onDialogPositiveClick(this,dateTime)
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                        listener.onDialogNegativeClick(this,dateTime)
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}