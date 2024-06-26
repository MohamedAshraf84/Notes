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

    val sortedNotes: MutableLiveData<List<NoteEntity>> by lazy {
        MutableLiveData<List<NoteEntity>>()
    }

    private var isActionModeEnabled = MutableLiveData<Boolean>(false)
    private var selectedNotes = MutableLiveData<HashSet<NoteEntity>>()
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
            }
        }
    }

    fun sortNotesByTitle()
    {
        viewModelScope.launch(Dispatchers.IO) {
            val notes = noteRepository.sortNotesByTitle()
            withContext(Dispatchers.Main) {
                sortedNotes.value = notes
            }
        }
    }

    fun sortNotesByCreationDate()
    {
        viewModelScope.launch(Dispatchers.IO) {
            val notes = noteRepository.sortNotesByCreationDate()
            withContext(Dispatchers.Main) {
                sortedNotes.value = notes
            }
        }
    }

    fun sortNotesByModificationDate()
    {
        viewModelScope.launch(Dispatchers.IO) {
            val notes = noteRepository.sortNotesByModificationDate()
            withContext(Dispatchers.Main) {
                sortedNotes.value = notes
            }
        }
    }

    fun deleteNoteById(noteId: Long)
    {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNoteById(noteId)
        }
    }

    fun pinNoteById(noteId: Long)
    {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.pinNoteById(noteId)
        }
    }

    fun updateNote(note : NoteEntity)
    {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.updateNote(note)
        }
    }

    fun setActionMode(isActionMode: Boolean)
    {
        isActionModeEnabled.value = isActionMode
    }

    fun setSelectedNotes(notes: HashSet<NoteEntity>)
    {
        selectedNotes.value = notes
    }

    fun getActionModeState(): MutableLiveData<Boolean> = isActionModeEnabled
    fun getSelectedNotes(): MutableLiveData<HashSet<NoteEntity>> = selectedNotes
}