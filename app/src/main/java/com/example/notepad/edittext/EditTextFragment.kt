package com.example.notepad.edittext

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Build.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.notepad.R
import com.example.notepad.reminder.DateTime
import com.example.notepad.database.Note
import com.example.notepad.database.NoteDatabase
import com.example.notepad.databinding.FragmentEditTextBinding


class EditTextFragment : Fragment() ,
TimePickerDialog.OnTimeSetListener,
DatePickerDialog.OnDateSetListener{

    private lateinit var binding:FragmentEditTextBinding
    private lateinit var viewModel: EditTextViewModel
    private lateinit var viewModelFactory: EditTextViewModelFactory
    private lateinit var note : Note
    private lateinit var dateTime: DateTime

    @RequiresApi(VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_text,container,false)
        val application = requireNotNull(this.activity).application
        val args = EditTextFragmentArgs.fromBundle(requireArguments())
        val dataSource = NoteDatabase.getInstance(application).noteDatabaseDao
        viewModelFactory = EditTextViewModelFactory(dataSource,application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(EditTextViewModel::class.java)


        note=args.note
        dateTime = DateTime()
        binding.viewmodel=viewModel
        binding.lifecycleOwner=this
        if(note.year==null)
        updateNote(dateTime)
        setNoteData()
        setReminderTextAndIcon()


        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        //Menu
        binding.topAppBar.setNavigationOnClickListener {
                viewModel.onBack(note)
        }

        binding.topAppBar.setOnMenuItemClickListener{item: MenuItem ->
            when(item.itemId){
                R.id.saveButton->{
                    note.noteBody = binding.noteBody.editText?.text.toString()
                    note.noteHeading = binding.noteHeading.editText?.text.toString()
                    viewModel.onSave(note)
                    true
                }R.id.ReminderButton -> {
                    if(note.reminder!!){
                        note.reminder=false
                        setReminderTextAndIcon()
                    }else {
                        dateTimePicker()
                    }
                    true
            }
                else->false
            }
        }

      viewModel.navigateToDashboard.observe(viewLifecycleOwner, Observer {
            if(it==true) {
                view?.hideKeyboard()
                this.findNavController().navigate(
                    EditTextFragmentDirections
                        .actionEditTextFragmentToDashboardFragment()
                )
                viewModel.onDoneNavigation()
            }
        })

        return binding.root
    }



    private fun setNoteData(){
        if(note.noteBody.isNotEmpty() || note.noteHeading.isNotEmpty()){
            binding.noteBody.editText?.setText(note.noteBody)
            binding.noteHeading.editText?.setText(note.noteHeading)
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun setReminderTextAndIcon(){
        Log.i("EditTextFragment","$note")
        if(note.reminder!!){
            binding.reminderText.text =
                getString(R.string.reminder_text,note.day,note.month,note.year,note.hour, note.minute)
            binding.reminderText.visibility = View.VISIBLE
            binding.topAppBar.menu.findItem(R.id.ReminderButton).setIcon(R.drawable.ic_baseline_alarm_off_24)

        }else{
            binding.reminderText.visibility = View.INVISIBLE
            binding.topAppBar.menu.findItem(R.id.ReminderButton).setIcon(R.drawable.ic_baseline_alarm_add_24)
        }
    }

    private fun updateNote(dateTime: DateTime){
        note.year=dateTime.year
        note.month=dateTime.month
        note.day=dateTime.day
        note.hour=dateTime.hour
        note.minute=dateTime.minute
        note.reminder=dateTime.isSet
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        dateTime.isSet=true
        dateTime.hour=hourOfDay
        dateTime.minute=minute
        updateNote(dateTime)
        setReminderTextAndIcon()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dateTime.isSet=true
        dateTime.year=year
        dateTime.month=month
        dateTime.day=dayOfMonth
        timePicker()
    }

    private fun dateTimePicker(){
        val dateDialog = DatePickerDialog(requireContext(),this,
        dateTime.year,dateTime.month,dateTime.day)
        dateDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(R.string.cancel),
        DialogInterface.OnClickListener{
                dialog, _ ->
            // User cancelled the dialog
            dateTime.isSet=false
            updateNote(dateTime)
            setReminderTextAndIcon()
            dialog.dismiss()
        })
        dateDialog.show()
    }

    private fun timePicker(){
        val dialog = TimePickerDialog(requireContext(), this,
            dateTime.hour,dateTime.minute,true)
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(R.string.cancel),
            DialogInterface.OnClickListener{
                    dialog, _ ->
                // User cancelled the dialog
                dateTime.isSet=false
                updateNote(dateTime)
                setReminderTextAndIcon()
                dialog.dismiss()
            })
        dialog.show()
    }

    private fun createChannel(channelId : String, channelName : String) {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // TODO: Step 2.4 change importance
                NotificationManager.IMPORTANCE_HIGH
            )// TODO: Step 2.6 disable badges for this channel
                .apply {
                    setShowBadge(false)
                }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.reminder_channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(notificationChannel)

        }
    }
}
