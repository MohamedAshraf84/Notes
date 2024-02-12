package com.mohamedashraf.notes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {
    abstract val notesDao : NotesDatabaseDao

    companion object {
        @Volatile
        private var daoInstance:NotesDatabaseDao? = null

        private fun buildDatabase(context: Context) : NotesDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                NotesDatabase::class.java,
                "Notes.db").build()


        fun getNotesDaoInstance(context: Context):NotesDatabaseDao
        {
            synchronized(this)
            {
                if (daoInstance == null) {
                    daoInstance = buildDatabase(context).notesDao
                }
                return daoInstance as NotesDatabaseDao
            }
        }
    }
}