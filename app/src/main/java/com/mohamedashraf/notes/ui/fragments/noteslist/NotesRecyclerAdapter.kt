package com.mohamedashraf.notes.ui.fragments.noteslist

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mohamedashraf.notes.R
import com.mohamedashraf.notes.database.NoteEntity
import com.mohamedashraf.notes.databinding.ItemNoteListBinding

class NotesRecyclerAdapter(private val context:Context, private var notesList:ArrayList<NoteEntity> = ArrayList<NoteEntity>())
    : RecyclerView.Adapter<NotesRecyclerAdapter.NoteViewHolder>(){
    private val TAG : String = "Adapter"
    private lateinit var itemNoteBinding : ItemNoteListBinding
    private lateinit var deleteClickedListener : OnItemDeleteClickedListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {

        Log.d(TAG, "onCreateViewHolder: ")
        return NoteViewHolder(LayoutInflater.from(context).inflate(R.layout.item_note_list, parent, false))
    }

    override fun onBindViewHolder(noteViewHolder: NoteViewHolder, position: Int) {

        Log.d(TAG, "onBindViewHolder, pos = ${position}")
        val note: NoteEntity = notesList[position]
        noteViewHolder.bind(position, note)
        noteViewHolder.setListeners()

        noteViewHolder.itemView.findViewById<CardView>(R.id.note_view).setOnClickListener()
        {
            val action = NotesFragmentDirections.actionNotesListToEditNote(note)
            noteViewHolder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int = notesList.size

    fun setList(notes: ArrayList<NoteEntity>)
    {
        notesList = notes
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(itemView: View) : ViewHolder(itemView), View.OnClickListener  {

        private var currentPosition = -1
        private lateinit var currentNote: NoteEntity
        private val txtNoteTitle = itemView.findViewById<TextView>(R.id.txt_note_title)
        private val txtNoteBody = itemView.findViewById<TextView>(R.id.txt_note_details)

        private val txtNoteLink = itemView.findViewById<TextView>(R.id.tv_note_link)
        private val ivNoteImage = itemView.findViewById<ImageView>(R.id.iv_note_image)

        private val txtNoteCreationDate = itemView.findViewById<TextView>(R.id.txt_note_creation_date)
        private val txtNoteCreationTime = itemView.findViewById<TextView>(R.id.txt_note_creation_time)
        //private val ivDelete = itemView.findViewById<ImageView>(R.id.iv_delete)
        private val noteView = itemView.findViewById<CardView>(R.id.note_view)

        fun bind(position: Int, note: NoteEntity)
        {
            currentPosition = position
            currentNote = note

            txtNoteTitle.text = note.noteTitle
            txtNoteBody.text = note.noteBody

            txtNoteLink.text = note.noteAttachedLink
            ivNoteImage.setImageURI(note.noteImagePath.toUri())

            txtNoteCreationDate.text = note.creationDate
            txtNoteCreationTime.text = note.creationTime
        }

        fun setListeners() {
            //ivDelete.setOnClickListener(this@NoteViewHolder)
            //noteView.setOnClickListener(this@NoteViewHolder)
        }

        override fun onClick(view: View?) {
            when(view?.id)
            {
                /*R.id.iv_delete -> {
                    Toast.makeText(context, "Delete Clicked: ${currentPosition}", Toast.LENGTH_LONG).show()
                    deleteNote(currentPosition)
                    deleteClickedListener.let {
                        deleteClickedListener.onClick(currentNote.noteId)
                    }
                }*/
            }
        }
    }

    fun deleteNote(position: Int)
    {
        notesList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, notesList.size)
    }
    fun setOnItemDeleteClickedListener(listener: OnItemDeleteClickedListener)
    {
        deleteClickedListener = listener
    }
    interface OnItemDeleteClickedListener
    {
        fun onClick(noteId: Long)
    }

}