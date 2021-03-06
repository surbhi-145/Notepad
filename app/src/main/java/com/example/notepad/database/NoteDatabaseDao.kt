package com.example.notepad.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface NoteDatabaseDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateNote(note : Note)

    @Insert
    fun insertNote(note: Note)

    @Delete
    fun deleteNote(note : Note)

    @Query("SELECT * FROM notes_table ORDER BY id DESC")
    fun getAllNotes() : LiveData<List<Note>>

    @Query("SELECT * FROM notes_table WHERE id= :noteId")
    fun getNoteById(noteId : Long) : Note?

    @Query("SELECT * FROM notes_table ORDER BY id DESC LIMIT 1")
    fun getNewNote() : Note?

    @Query("DELETE FROM notes_table WHERE id= :noteId")
    fun deleteNoteWithId(noteId : Long)

    @Query("DELETE FROM notes_table WHERE (note_body=NULL or note_body='') AND (note_heading=NULL OR note_heading='')")
    fun deleteEmpty()
}