package com.example.notepad.viewtext

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notepad.R
import com.example.notepad.database.Note
import com.example.notepad.database.NoteDatabase
import com.example.notepad.databinding.FragmentViewTextBinding

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

        //menu
        binding.topAppBar.setNavigationOnClickListener {
            viewModel.onBack()
        }

        binding.topAppBar.setOnMenuItemClickListener{item: MenuItem ->
            when(item.itemId){
                R.id.shareButton->{
                    shareNote()
                    true
                }
                R.id.saveButton->{
                    viewModel.onEdit()
                    true
                }
                R.id.deleteButton->{
                    viewModel.onDelete(note)
                    true
                }
                else->false
            }
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


    private fun getShareIntent() : Intent {
        return ShareCompat.IntentBuilder.from(requireActivity())
            .setText(getString(R.string.share_note, note.noteHeading, note.noteBody))
            .setType("text/plain")
            .intent
    }

    private fun shareNote() {
        startActivity(getShareIntent())
    }
}


