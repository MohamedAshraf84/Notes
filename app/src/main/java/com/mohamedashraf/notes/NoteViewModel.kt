package com.mohamedashraf.notes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.room.RoomDatabase
import com.mohamedashraf.notes.database.NoteEntity
import com.mohamedashraf.notes.database.NotesDatabase
import com.mohamedashraf.notes.database.NotesDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel() : ViewModel()
{
    private val noteRepository : NoteRepository
    var allNotes : LiveData<ArrayList<NoteEntity>>
    val searchResults: MutableLiveData<List<NoteEntity>> by lazy {
        MutableLiveData<List<NoteEntity>>()
    }
    init {
        val notesDao = NotesDatabase.getNotesDaoInstance(NotesApplication.getApplicationContext())
        noteRepository = NoteRepository(notesDao)
        allNotes = noteRepository.allNotes
    }

    fun addNoteToDataBase(note : NoteEntity)
    {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.addNote(note)
        }
    }

    fun deleteNoteFromDataBase(note : NoteEntity)
    {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNote(note)
        }
    }

    fun searchNotes(searchKey : String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            val notes = noteRepository.searchNotes(searchKey)
            withContext(Dispatchers.Main) {
                searchResults.value = notes
                Log.d("DDD", "searchNotes: ${searchResults.value}")
            }
        }
    }

    fun deleteNoteById(noteId: Long)
    {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNoteById(noteId)
        }
    }

    fun updateNote(note : NoteEntity)
    {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.updateNote(note)
        }
    }

}