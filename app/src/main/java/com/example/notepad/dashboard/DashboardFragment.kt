package com.example.notepad.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.notepad.R
import com.example.notepad.database.NoteDatabase
import com.example.notepad.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var binding:FragmentDashboardBinding
    private lateinit var viewModelFactory: DashboardViewModelFactory
    private lateinit var viewModel: DashboardViewModel

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

        return binding.root


    }
}