package com.mohamedashraf.notes.ui.fragments.noteslist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mohamedashraf.notes.R
import com.mohamedashraf.notes.database.NoteEntity

class NotesRecyclerAdapter(private val context:Context, private var notesList:List<NoteEntity> = emptyList<NoteEntity>()) : RecyclerView.Adapter<NotesRecyclerAdapter.NoteViewHolder>() {
    inner class NoteViewHolder(itemView: View) : ViewHolder(itemView) {
        private var currentPosition = -1
        private lateinit var currentNote: NoteEntity
        private val txtNoteTitle = itemView.findViewById<TextView>(R.id.txt_note_title)
        private val txtNoteBody = itemView.findViewById<TextView>(R.id.txt_note_details)
        private val txtNoteCreationDate = itemView.findViewById<TextView>(R.id.txt_note_creation_date)
        private val txtNoteCreationTime = itemView.findViewById<TextView>(R.id.txt_note_creation_time)

        fun setData(position: Int, note: NoteEntity)
        {
            currentPosition = position
            currentNote = note

            txtNoteTitle.text = note.noteTitle
            txtNoteBody.text = note.noteBody
            txtNoteCreationDate.text = note.creationDate
            txtNoteCreationTime.text = note.creationTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(LayoutInflater.from(context).inflate(R.layout.item_note_list, parent, false))
    }

    override fun getItemCount(): Int = notesList.size

    override fun onBindViewHolder(noteViewHolder: NoteViewHolder, position: Int) {

        val note: NoteEntity = notesList[position]
        noteViewHolder.setData(position, note)
    }

    fun setData(notes: List<NoteEntity>?)
    {
        notesList = notes!!
        notifyDataSetChanged()
    }
}