package com.example.notepad.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDatabaseDao{

    @Update
    fun updateNote(note : Note)

    @Insert
    fun insertNote(note: Note)

    @Delete
    fun deleteNote(note : Note)

    @Query("SELECT * FROM notes_table")
    fun getAllNotes() : LiveData<List<Note>>

    @Query("SELECT * FROM notes_table WHERE id= :noteId")
    fun getNoteById(noteId : Long) : Note?


}