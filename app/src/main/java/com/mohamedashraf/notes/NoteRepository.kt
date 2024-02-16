package com.mohamedashraf.notes

import com.mohamedashraf.notes.database.NoteEntity
import com.mohamedashraf.notes.database.NotesDatabaseDao

class NoteRepository(private val notesDao : NotesDatabaseDao) {

    var allNotes = notesDao.getAllNotes()
    suspend fun addNote(noteToAdd: NoteEntity)
    {
        notesDao.insert(noteToAdd)
    }

    suspend fun deleteNote(noteToDelete: NoteEntity)
    {
        notesDao.delete(noteToDelete)
    }

    suspend fun updateNote(noteToUpdate : NoteEntity)
    {
        notesDao.update(noteToUpdate)
    }
}