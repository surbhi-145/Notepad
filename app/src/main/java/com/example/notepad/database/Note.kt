package com.example.notepad.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0L,

    @ColumnInfo(name = "note_heading")
    var noteHeading: String = "",

    @ColumnInfo (name = "note_body")
    var noteBody : String = ""
)