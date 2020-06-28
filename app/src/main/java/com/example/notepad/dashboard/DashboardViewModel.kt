package com.example.notepad.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.notepad.database.Note
import com.example.notepad.database.NoteDatabaseDao

class DashboardViewModel(
    val database: NoteDatabaseDao,
    application: Application) : AndroidViewModel(application){

    val notes= database.getAllNotes()

    fun onNoteClicked(note : Note){

    }

}