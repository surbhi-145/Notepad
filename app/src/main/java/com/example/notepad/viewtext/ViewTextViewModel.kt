package com.example.notepad.viewtext

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notepad.database.Note
import com.example.notepad.database.NoteDatabaseDao
import kotlinx.coroutines.*

class ViewTextViewModel(val database: NoteDatabaseDao,
                        application: Application
): AndroidViewModel(application){

    private var _navigateToDashboard = MutableLiveData<Boolean>()
    val navigateToDashboard : LiveData<Boolean>
        get() = _navigateToDashboard

    private var _navigateToEditFragment = MutableLiveData<Boolean>()
    val navigateToEditFragment : LiveData<Boolean>
        get()=_navigateToEditFragment

   private var job= Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun onEdit(){
        _navigateToEditFragment.value=true
    }

    fun onDelete(note : Note){
        uiScope.launch {
            withContext(Dispatchers.IO){
                note.let {
                    database.deleteNoteWithId(note.id)
                }
            }
            _navigateToDashboard.value=true
        }
    }

    fun onBack(){
        _navigateToDashboard.value=true
    }

    fun onDashboardNavigation(){
        _navigateToDashboard.value=false
    }

    fun onEditNavigation(){
        _navigateToEditFragment.value=false
    }
}