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

    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    if(note!= null) {
        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            note.id,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val doneIntent = Intent(applicationContext, DoneReceiver::class.java).apply {
            putExtra("ID", note.id)
            type = "$note"
        }
        val donePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            note.id,
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val builder = NotificationCompat.Builder(
            applicationContext, applicationContext.getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_baseline_notes_24)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_baseline_check_24,
                applicationContext.getString(R.string.done),
                donePendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(note.noteHeading)
            .setContentText(note.noteBody)

        notify(note.id, builder.build())
    }

}


