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

    private var _navigateToEditFragment = MutableLiveData<Boolean>()
    val navigateToEditFragment : LiveData<Boolean>
        get()=_navigateToEditFragment

    private var newNote=MutableLiveData<Note>()

    private var job= Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    fun onNoteClicked(note : Note){
            _navigateToViewFragment.value=note
    }

    fun doneViewNavigation(){
        _navigateToViewFragment.value=null
    }

    fun onAddButtonClicked(){
        _navigateToEditFragment.value=true
    }

    fun doneEditNavigation(){
        _navigateToEditFragment.value=false
        newNote.value=null
    }

    init {
        initializeNewNote()
    }

    private fun initializeNewNote(){
        uiScope.launch {
            newNote.value=getNewNote()
        }
    }

    fun createNewNote() : Note? {

        uiScope.launch {
            val note = Note()
            insert(note)
            newNote.value=note
        }
        return newNote.value
    }

    private suspend fun insert(note : Note){
        withContext(Dispatchers.IO){
            database.insertNote(note)
        }
    }

    private suspend fun getNewNote():Note?{
        return withContext(Dispatchers.IO){
            val note = database.getNewNote()
            note
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}