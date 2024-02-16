package com.mohamedashraf.notes.ui.fragments.editnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditNoteViewModel : ViewModel() {

    private val noteTitle = MutableLiveData<String>("")
    private val noteDetails = MutableLiveData<String>("")
    private val charactersCnt = MutableLiveData<Int>(0)

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