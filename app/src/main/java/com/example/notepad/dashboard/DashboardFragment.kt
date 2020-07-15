package com.example.notepad.dashboard

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notepad.R
import com.example.notepad.database.NoteDatabase
import com.example.notepad.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var binding:FragmentDashboardBinding
    private lateinit var viewModelFactory: DashboardViewModelFactory
    private lateinit var viewModel: DashboardViewModel
    private lateinit var adapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=DataBindingUtil.inflate(
            inflater,R.layout.fragment_dashboard,container,false)
        val application = requireNotNull(this.activity).application
        val dataSource = NoteDatabase.getInstance(application).noteDatabaseDao
        viewModelFactory = DashboardViewModelFactory(dataSource,application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(DashboardViewModel::class.java)

        binding.viewmodel=viewModel
        binding.lifecycleOwner=this


        //Menu
        binding.topAppBar.setOnMenuItemClickListener{item: MenuItem ->
            when(item.itemId){
                R.id.view_type ->{
                   viewModel.onViewTypeClicked()
                    true
                }
                else->false
            }
        }

        adapter= NotesAdapter(NoteListener { note ->
            viewModel.onNoteClicked(note)
        })

        binding.notesList.adapter=adapter
        val stagManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        val listViewManger = LinearLayoutManager(context)
        binding.notesList.layoutManager=stagManager

        viewModel.notes.observe(viewLifecycleOwner,Observer{
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.isGridView.observe(viewLifecycleOwner, Observer {it->
            if(it == true){
                binding.notesList.layoutManager=stagManager
                binding.topAppBar.menu.findItem(R.id.view_type).setIcon(R.drawable.ic_baseline_view_list_24)
            }else{
                binding.notesList.layoutManager=listViewManger
                binding.topAppBar.menu.findItem(R.id.view_type).setIcon(R.drawable.ic_baseline_grid_on_24)
            }

        })

        viewModel.navigateToEditFragment.observe(viewLifecycleOwner, Observer {note ->
                    note?.let {
                        this.findNavController().navigate(
                            DashboardFragmentDirections
                                .actionDashboardFragmentToEditTextFragment(note)
                        )
                        viewModel.doneEditNavigation()
                    }
        })

        viewModel.navigateToViewFragment.observe(viewLifecycleOwner, Observer {note->
            note?.let{
                this.findNavController().navigate(DashboardFragmentDirections
                    .actionDashboardFragmentToViewTextFragment(note))
                viewModel.doneViewNavigation()
            }
        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.deleteEmpty()
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}