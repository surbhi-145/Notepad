package com.example.notepad.edittext

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notepad.database.Note
import com.example.notepad.database.NoteDatabaseDao
import kotlinx.coroutines.*

class EditTextViewModel(val database: NoteDatabaseDao,
                        val noteId : Long,
                        application: Application):
    AndroidViewModel(application){

    private var _navigateToDashboard = MutableLiveData<Boolean>()
    val navigateToDashboard : LiveData<Boolean>
        get() = _navigateToDashboard

    lateinit var  currNote: Note
    private var job= Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    init {
        initializeCurrNote()
    }

    private fun  initializeCurrNote(){
        uiScope.launch {
            val tmpNote=getCurrNote()
            tmpNote?.let {
                currNote=tmpNote
            }
        }
    }

    private suspend fun getCurrNote() : Note?{
         return withContext(Dispatchers.IO){
            val note= database.getNoteById(noteId)
             note
        }
    }

    fun onSave(){

        uiScope.launch {
            withContext(Dispatchers.IO){
                currNote.let {
                    database.updateNote(currNote)
                }
            }
        }

        navigate()

    }

    fun onDelete(){
        uiScope.launch {
            withContext(Dispatchers.IO){
                currNote.let {
                    database.deleteNote(currNote)
                }
            }
        }
        navigate()
    }


    private fun navigate(){
        _navigateToDashboard.value=true
    }

    fun onDoneNavigation(){
        _navigateToDashboard.value=false
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}