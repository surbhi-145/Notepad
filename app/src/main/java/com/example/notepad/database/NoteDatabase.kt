package com.example.notepad.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Note ::class], version = 2, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    abstract val noteDatabaseDao : NoteDatabaseDao

    companion object{

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notes_table ADD COLUMN year INTEGER ")
                database.execSQL("ALTER TABLE notes_table ADD COLUMN month INTEGER ")
                database.execSQL("ALTER TABLE notes_table ADD COLUMN day INTEGER ")
                database.execSQL("ALTER TABLE notes_table ADD COLUMN hour INTEGER ")
                database.execSQL("ALTER TABLE notes_table ADD COLUMN minute INTEGER ")
                database.execSQL("ALTER TABLE notes_table ADD COLUMN reminder INTEGER")
            }
        }

        @Volatile
        private var INSTANCE : NoteDatabase? = null

        fun getInstance(context: Context) : NoteDatabase {

            kotlin.synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NoteDatabase::class.java,
                        "notes_database"
                    ).addMigrations(MIGRATION_1_2)
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }

    }

}