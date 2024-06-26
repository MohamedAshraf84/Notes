package com.mohamedashraf.notes

import androidx.lifecycle.map
import com.mohamedashraf.notes.database.NoteEntity
import com.mohamedashraf.notes.database.NotesDatabaseDao

class NoteRepository(private val notesDao : NotesDatabaseDao) {

    var allNotes = notesDao.getAllNotes().map { list -> ArrayList<NoteEntity>(list) }
    suspend fun addNote(noteToAdd: NoteEntity)
    {
        notesDao.insert(noteToAdd)
    }

    suspend fun deleteNote(noteToDelete: NoteEntity)
    {
        notesDao.delete(noteToDelete)
    }

    suspend fun deleteNoteById(noteId: Long)
    {
        notesDao.deleteNoteById(noteId)
    }

    suspend fun pinNoteById(noteId: Long)
    {
        val note = notesDao.getNoteById(noteId)
        note?.apply {
            isPinned = true
        }
        note?.let {
            notesDao.update(note)
        }
    }

    suspend fun updateNote(noteToUpdate : NoteEntity)
    {
        notesDao.update(noteToUpdate)
    }

    suspend fun searchNotes(searchKey : String) : List<NoteEntity>
    {
        return notesDao.searchNotes(searchKey)
    }


    suspend fun sortNotesByTitle() : List<NoteEntity>
    {
        return notesDao.sortNotesByTitle()
    }

    suspend fun sortNotesByCreationDate() : List<NoteEntity>
    {
        return notesDao.sortNotesByCreationDate()
    }

    suspend fun sortNotesByModificationDate() : List<NoteEntity>
    {
        return notesDao.sortNotesByModificationDate()
    }
}