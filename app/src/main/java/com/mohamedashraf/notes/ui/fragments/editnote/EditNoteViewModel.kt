package com.mohamedashraf.notes.ui.fragments.editnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditNoteViewModel : ViewModel() {

    private val noteTitle = MutableLiveData<String>("")
    private val noteDetails = MutableLiveData<String>("")
    private val charactersCnt = MutableLiveData<Int>(0)

    fun setNoteTitle(title:String)
    {
        noteTitle.value = title
    }

    fun setNoteDetails(details:String)
    {
        noteDetails.value = details
    }

    fun setCharsCounter(charsCounter:Int)
    {
        charactersCnt.value = charsCounter
    }


    fun getNoteTitle(): MutableLiveData<String> = noteTitle
    fun getNoteDetails(): MutableLiveData<String> = noteDetails
    fun getCharsCounter(): MutableLiveData<Int> = charactersCnt

}