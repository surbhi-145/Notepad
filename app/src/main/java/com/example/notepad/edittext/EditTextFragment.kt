package com.example.notepad.edittext

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notepad.R
import com.example.notepad.database.NoteDatabase
import com.example.notepad.databinding.FragmentEditTextBinding

class EditTextFragment : Fragment(){

    private lateinit var binding:FragmentEditTextBinding
    private lateinit var viewModel: EditTextViewModel
    private lateinit var viewModelFactory: EditTextViewModelFactory

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
        viewModelFactory = EditTextViewModelFactory(dataSource,args.noteId,application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(EditTextViewModel::class.java)

        binding.viewmodel=viewModel
        binding.lifecycleOwner=this

        binding.saveButton.setOnClickListener() {
            viewModel.currNote.noteBody = binding.noteBody.text.toString()
            viewModel.currNote.noteHeading = binding.noteHeading.text.toString()
            viewModel.onSave()
        }

      viewModel.navigateToDashboard.observe(viewLifecycleOwner, Observer {
            if(it==true) {
                this.findNavController().navigate(
                    EditTextFragmentDirections
                        .actionEditTextFragmentToDashboardFragment()
                )
                viewModel.onDoneNavigation()
            }
        })
        return binding.root
    }


}