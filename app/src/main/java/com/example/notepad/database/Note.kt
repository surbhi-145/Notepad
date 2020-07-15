package com.example.notepad.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "notes_table")
data class Note(

    @PrimaryKey(autoGenerate = true)
    var id : Long = 0L ,

    @ColumnInfo(name = "note_heading")
    var noteHeading: String = "",

    @ColumnInfo (name = "note_body")
    var noteBody : String = "",

    @ColumnInfo(name = "year")
    var year : Int? = null,

    @ColumnInfo(name = "month")
    var month : Int? = null,

    @ColumnInfo(name = "day")
    var day : Int? = null,

    @ColumnInfo(name = "hour")
    var hour : Int? = null,

    @ColumnInfo(name = "minute")
    var minute : Int? = null,

    @ColumnInfo(name = "reminder")
    var  reminder: Boolean? = null

) : Parcelable