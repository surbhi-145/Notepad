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

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    private var note = Note()
    val notes= database.getAllNotes()

    private var _isGridView = MutableLiveData<Boolean>()
    val isGridView : LiveData<Boolean>
        get()=_isGridView

    private var _navigateToViewFragment = MutableLiveData<Note>()
    val navigateToViewFragment : LiveData<Note>
            get()=_navigateToViewFragment

    private var _navigateToEditFragment = MutableLiveData<Note>()
    val navigateToEditFragment : LiveData<Note>
        get()=_navigateToEditFragment

    private suspend fun initializeNote() : Note? {
        return withContext(Dispatchers.IO){
            database.updateNote(note)
            val currNote = database.getNewNote()
            currNote
        }
    }

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
            uiScope.launch {
                note = initializeNote()!!
                _navigateToEditFragment.value=note
            }
    }

    fun onViewTypeClicked(){
        _isGridView.value = !(_isGridView.value ==null || _isGridView.value==true)
    }

}