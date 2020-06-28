package com.example.notepad.dashboard

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notepad.database.NoteDatabaseDao

class DashboardViewModelFactory(
    private val dataSource: NoteDatabaseDao,
    private val application: Application) : ViewModelProvider.Factory{
    @Suppress("unchecked cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DashboardViewModel :: class.java)){
            return DashboardViewModel(dataSource,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
