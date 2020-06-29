package com.example.notepad.edittext

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notepad.database.Note
import com.example.notepad.database.NoteDatabaseDao

class EditTextViewModelFactory(
    private val dataSource: NoteDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditTextViewModel::class.java)) {
            return EditTextViewModel(dataSource,  application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
