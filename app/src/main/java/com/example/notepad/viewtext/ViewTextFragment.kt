package com.example.notepad.viewtext

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
import com.example.notepad.database.Note
import com.example.notepad.database.NoteDatabase
import com.example.notepad.databinding.FragmentViewTextBinding
import kotlinx.android.synthetic.main.fragment_view_text.*
import java.util.zip.Inflater

class ViewTextFragment : Fragment(){

    private lateinit var binding : FragmentViewTextBinding
    private lateinit var viewModel: ViewTextViewModel
    private lateinit var viewModelFactory: ViewTextViewModelFactory
    private lateinit var note : Note

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding=DataBindingUtil.inflate(
            inflater, R.layout.fragment_view_text, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = NoteDatabase.getInstance(application).noteDatabaseDao
        val args = ViewTextFragmentArgs.fromBundle(requireArguments())
        viewModelFactory= ViewTextViewModelFactory(dataSource,application)
        viewModel=ViewModelProvider(this,viewModelFactory).get(ViewTextViewModel::class.java)

        note= args.note
        binding.viewModel=viewModel
        binding.lifecycleOwner=this
        setNoteData()

        binding.deleteButton.setOnClickListener(){
            viewModel.onDelete(note)
        }

        viewModel.navigateToDashboard.observe(viewLifecycleOwner, Observer { it ->
            if(it==true){
                this.findNavController().navigate(
                    ViewTextFragmentDirections.actionViewTextFragmentToDashboardFragment()
                )
                viewModel.onDashboardNavigation()
            }
        })

        viewModel.navigateToEditFragment.observe(viewLifecycleOwner, Observer {it ->
            if(it== true){
                this.findNavController().navigate(
                    ViewTextFragmentDirections.actionViewTextFragmentToEditTextFragment(note)
                )
                viewModel.onEditNavigation()
            }
        })

        return binding.root
    }

    private fun setNoteData(){
        binding.noteHeading.text=note.noteHeading
        binding.noteBody.text=note.noteBody
    }
}


