package com.mohamedashraf.notes.ui.fragments.editnote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedashraf.notes.NotesApplication
import com.mohamedashraf.notes.database.NoteEntity
import com.mohamedashraf.notes.database.NotesDatabase
import com.mohamedashraf.notes.database.NotesDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditNoteViewModel : ViewModel() {

    private val noteTitle = MutableLiveData<String>("")
    private val noteDetails = MutableLiveData<String>("")
    private val charactersCnt = MutableLiveData<Int>(0)
    private var noteDao: NotesDatabaseDao? = null

    init {
        noteDao = NotesDatabase.getNotesDaoInstance(NotesApplication.getApplicationContext())
    }

    fun updateNoteTitle(title:String)
    {
        noteTitle.value = title
        updateCharsCounter(noteTitle.value!!.length + (noteDetails.value?.length ?: 0))
    }

    fun updateNoteDetails(details:String)
    {
        noteDetails.value = details
        updateCharsCounter(noteTitle.value!!.length + (noteDetails.value?.length ?: 0))
    }

    private fun updateCharsCounter(charsCounter:Int)
    {
        this.charactersCnt.value = charsCounter
    }


    fun getNoteTitle(): MutableLiveData<String> = noteTitle
    fun getNoteDetails(): MutableLiveData<String> = noteDetails
    fun getCharsCounter(): MutableLiveData<Int> = charactersCnt

}