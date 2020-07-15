package com.example.notepad.reminder

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.notepad.MainActivity
import com.example.notepad.R
import com.example.notepad.database.Note

fun NotificationManager.sendNotification(note: Note?, applicationContext: Context){


    if(note!= null) {

        val doneIntent = Intent(applicationContext, DoneReceiver::class.java).apply {
            putExtra("ID", note.id)
            type = "$note"
        }
        val donePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            note.id.toInt(),
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java).apply {
            putExtra("ID", note.id)
            type = "$note"
        }
        val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            note.id.toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val builder = NotificationCompat.Builder(
            applicationContext, applicationContext.getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_baseline_notes_24)
            .addAction(
                R.drawable.ic_baseline_check_24,
                applicationContext.getString(R.string.done),
                donePendingIntent
            )
            .addAction(
                R.drawable.ic_baseline_alarm_off_24,
                applicationContext.getString(R.string.snooze),
                snoozePendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(note.noteHeading)
            .setContentText(note.noteBody)

        notify(note.id.toInt(), builder.build())
    }

}


