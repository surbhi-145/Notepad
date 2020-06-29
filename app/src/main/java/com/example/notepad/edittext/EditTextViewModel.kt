package com.example.notepad.edittext

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notepad.database.Note
import com.example.notepad.database.NoteDatabaseDao
import kotlinx.coroutines.*

class EditTextViewModel(val database: NoteDatabaseDao,
                        application: Application):
    AndroidViewModel(application){

    private var _navigateToDashboard = MutableLiveData<Boolean>()
    val navigateToDashboard : LiveData<Boolean>
        get() = _navigateToDashboard

    private var job= Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    fun onSave(note : Note){
        uiScope.launch {
            withContext(Dispatchers.IO){
                        database.updateNote(note) ?: return@withContext

            }
            _navigateToDashboard.value=true
        }
    }

    fun onDelete(note : Note){
        uiScope.launch {
            withContext(Dispatchers.IO){
                        database.deleteNoteWithId(note.id) ?: return@withContext
            }
            _navigateToDashboard.value=true
        }
    }


    fun onDoneNavigation(){
        _navigateToDashboard.value=false
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}