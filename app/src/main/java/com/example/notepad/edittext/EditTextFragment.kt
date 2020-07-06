package com.example.notepad.edittext

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notepad.R
import com.example.notepad.ReminderDialog.DateTime
import com.example.notepad.ReminderDialog.ReminderDialogFragment
import com.example.notepad.database.Note
import com.example.notepad.database.NoteDatabase
import com.example.notepad.databinding.FragmentEditTextBinding

class EditTextFragment : Fragment() ,
    ReminderDialogFragment.ReminderDialogListener{

    private lateinit var binding:FragmentEditTextBinding
    private lateinit var viewModel: EditTextViewModel
    private lateinit var viewModelFactory: EditTextViewModelFactory
    private lateinit var note : Note

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
        binding.viewmodel=viewModel
        binding.lifecycleOwner=this
        setNoteData()
        setReminderText()

        //Menu
        binding.topAppBar.setNavigationOnClickListener {
                viewModel.onBack()
        }

        binding.topAppBar.setOnMenuItemClickListener{item: MenuItem ->
            when(item.itemId){
                R.id.saveButton->{
                    note.noteBody = binding.noteBody.editText?.text.toString()
                    note.noteHeading = binding.noteHeading.editText?.text.toString()
                    viewModel.onSave(note)
                    true
                }R.id.ReminderButton -> {
                    val dialog  = ReminderDialogFragment(this)
                    dialog.show(parentFragmentManager,"Reminder Dialog")
                    true
            }
                else->false
            }
        }

      viewModel.navigateToDashboard.observe(viewLifecycleOwner, Observer {
            if(it==true) {
                view?.hideKeyboard()
                if(note.reminder){
                    //TODO
                }
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

    override fun onDialogPositiveClick(dateTime: DateTime) {
        updateNote(dateTime)
        setReminderText()
    }

    override fun onDialogNegativeClick(dateTime: DateTime) {
         updateNote(dateTime)
        setReminderText()
    }

    private fun setReminderText(){
        if(note.reminder){

            binding.reminderText.text =
                getString(R.string.reminder_text,note.day,note.month,note.year,note.hour, note.minute)
            binding.reminderText.visibility = View.VISIBLE
        }else{
            binding.reminderText.visibility = View.INVISIBLE
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

}
