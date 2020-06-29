package com.example.notepad.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notepad.database.Note
import com.example.notepad.database.NoteDatabaseDao
import kotlinx.coroutines.*

class DashboardViewModel(
    val database: NoteDatabaseDao,
    application: Application) : AndroidViewModel(application){

    val notes= database.getAllNotes()

    private var _navigateToViewFragment = MutableLiveData<Note>()
    val navigateToViewFragment : LiveData<Note>
            get()=_navigateToViewFragment

    private var _navigateToEditFragment = MutableLiveData<Note>()
    val navigateToEditFragment : LiveData<Note>
        get()=_navigateToEditFragment

    fun onNoteClicked(note : Note){
            _navigateToViewFragment.value=note
    }

    fun doneViewNavigation(){
        _navigateToViewFragment.value=null
    }


    fun doneEditNavigation(){
        _navigateToEditFragment.value=null

    }

    fun onAddButtonClicked()  {
            val note = Note()
            _navigateToEditFragment.value=note

    }


}