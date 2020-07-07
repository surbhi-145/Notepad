package com.example.notepad.reminder

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.notepad.database.Note
import com.example.notepad.database.NoteDatabase
import kotlinx.coroutines.*
import java.util.*

class AlarmReceiver : BroadcastReceiver(){
    private var job= Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onReceive(context: Context, intent: Intent?) {

        if (intent != null) {
            if(intent.extras != null) {
                val id : Int  = intent.extras!!.get("ID") as Int
                Log.i("Receiver","Receiver called , $id")
                val notificationManager = ContextCompat.getSystemService(
                    context,
                    NotificationManager::class.java
                ) as NotificationManager

                val dataSource = NoteDatabase.getInstance(context).noteDatabaseDao
                var note : Note? = null
                uiScope.launch {
                    withContext(Dispatchers.IO){
                        note = dataSource.getNoteById(id)
                    }
                    Log.i("Receiver","Notify called, $note")
                    notificationManager.sendNotification(note,context)
                    job.cancel()
                }
            }
        }

    }
}

class DoneReceiver : BroadcastReceiver(){
    private var job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if (intent.extras != null) {
                val id: Int = intent.extras!!.get("ID") as Int
                Log.i("Receiver", "Done Receiver called , $id")

                val notificationManager = ContextCompat.getSystemService(
                    context!!,
                    NotificationManager::class.java
                ) as NotificationManager

                val dataSource = context.let { NoteDatabase.getInstance(it).noteDatabaseDao }
                var note: Note? = null
                uiScope.launch {
                    withContext(Dispatchers.IO) {
                        note = dataSource.getNoteById(id)
                        note?.reminder=false
                        dataSource.updateNote(note!!)
                    }
                    Log.i("Receiver", "Done called, $note")
                    notificationManager.cancel(note!!.id)
                    job.cancel()
                }

            }
        }
    }
}