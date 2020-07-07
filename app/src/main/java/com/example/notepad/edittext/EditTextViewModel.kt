package com.example.notepad.edittext

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notepad.database.Note
import com.example.notepad.database.NoteDatabaseDao
import com.example.notepad.reminder.AlarmReceiver
import com.example.notepad.reminder.DateTime
import kotlinx.coroutines.*
import java.util.*

class EditTextViewModel(val database: NoteDatabaseDao,
                        application: Application):
    AndroidViewModel(application){

    private var _navigateToDashboard = MutableLiveData<Boolean>()
    val navigateToDashboard : LiveData<Boolean>
        get() = _navigateToDashboard

    private lateinit var notifyPendingIntent : PendingIntent

    private val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(application, AlarmReceiver::class.java)

    private var job= Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private fun createPendingIntent(note : Note){
        notifyIntent.apply{
            putExtra("ID",note.id)
            Log.i("Receiver","Add Extra to intent , ${note.id}")
            type = "$note"
        }
        notifyPendingIntent =PendingIntent.getBroadcast(
            getApplication(),
            note.id,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun onSave(note : Note){

        uiScope.launch {
            if(note.noteBody.isNotEmpty() || note.noteHeading.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    database.updateNote(note)
                }
            }else{
                    withContext(Dispatchers.IO){
                        database.deleteNote(note)
                    }
            }

            Log.i("EditTextFragment","$note")
            if(note.reminder){
                scheduleAlarm(note)
            }else{
                cancelAlarm(note)
            }
            _navigateToDashboard.value=true
        }
    }

    fun onBack(note : Note){
        uiScope.launch {
            withContext(Dispatchers.IO){
                database.deleteNote(note)
            }
            _navigateToDashboard.value=true
        }
    }

    fun onDoneNavigation(){
        _navigateToDashboard.value=false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleAlarm(note : Note){

        createPendingIntent(note)

        val c = Calendar.getInstance(Locale.getDefault())
        c.timeInMillis= System.currentTimeMillis()
        note.hour?.let { c.set(Calendar.HOUR_OF_DAY, it) }
        note.minute?.let { c.set(Calendar.MINUTE, it) }
        note.year?.let { c.set(Calendar.YEAR, it) }
        note.month?.let { c.set(Calendar.MONTH, it) }
        note.day?.let { c.set(Calendar.DAY_OF_MONTH, it) }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,c.timeInMillis,notifyPendingIntent)
        Log.i("Receiver","Alarm scheduled , ${note.id} ${note.hour} ${note.minute}")
    }

    private fun cancelAlarm(note: Note){
        createPendingIntent(note)
        alarmManager.cancel(notifyPendingIntent)

    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}