package com.example.notepad.reminder

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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

class SnoozeReceiver : BroadcastReceiver(){
    private var job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if (intent.extras != null) {
                val id: Int = intent.extras!!.get("ID") as Int
                Log.i("Receiver", "Done Receiver called , $id")

                val notificationManager = ContextCompat.getSystemService(
                    context!!,
                    NotificationManager::class.java
                ) as NotificationManager
                val notifyIntent = Intent(context, AlarmReceiver::class.java)


                val dataSource = context.let { NoteDatabase.getInstance(it).noteDatabaseDao }
                var note: Note? = null
                uiScope.launch {
                    withContext(Dispatchers.IO) {
                        note = dataSource.getNoteById(id)
                        val min = note?.minute?.plus(1)
                        if (min != null) {
                            note?.minute = min % 60
                            val hour = note?.hour
                            if (hour != null) {
                                note?.hour = (hour + (min/60)) % 24
                            }
                        }
                        dataSource.updateNote(note!!)
                    }
                    Log.i("Receiver", "Snooze called, $note")
                    notifyIntent.apply{
                        putExtra("ID",note!!.id)
                        Log.i("Receiver","Add Extra to intent , ${note!!.id}")
                        type = "$note"
                    }
                    val notifyPendingIntent =PendingIntent.getBroadcast(
                        context,
                        note!!.id,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT)
                    scheduleAlarm(note!!,notifyPendingIntent,context)
                    notificationManager.cancel(note!!.id)
                    job.cancel()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleAlarm(note : Note, notifyPendingIntent: PendingIntent, context: Context){

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
}