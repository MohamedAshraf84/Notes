package com.mohamedashraf.notes.ui.fragments.noteslist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mohamedashraf.notes.R
import com.mohamedashraf.notes.database.NoteEntity
import com.mohamedashraf.notes.databinding.ItemNoteListBinding

class NotesRecyclerAdapter(private val context:Context, private var notes:ArrayList<NoteEntity> = ArrayList<NoteEntity>())
    : RecyclerView.Adapter<NotesRecyclerAdapter.NoteViewHolder>(){

    private val TAG : String = "Adapter"

    var isMultiSelectEnabled : Boolean = false
    private lateinit var itemNoteBinding : ItemNoteListBinding
    private var notesAdapterInteractionListener: NotesAdapterInteractionListener? = null
    private var selectedNotes: HashSet<NoteEntity> = HashSet<NoteEntity>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(LayoutInflater.from(context).inflate(R.layout.item_note_list, parent, false))
    }

    override fun onBindViewHolder(noteViewHolder: NoteViewHolder, position: Int) {
        val note: NoteEntity = notes[position]
        noteViewHolder.bind(position, note)
        noteViewHolder.setListeners()
    }


    override fun getItemCount(): Int = notes.size

    fun setList(notes: ArrayList<NoteEntity>)
    {
        this.notes = notes
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(itemView: View) : ViewHolder(itemView), View.OnClickListener  {

        private var currentPosition = -1
        private lateinit var currentNote: NoteEntity
        private val txtNoteTitle = itemView.findViewById<TextView>(R.id.txt_note_title)
        private val txtNoteBody = itemView.findViewById<TextView>(R.id.txt_note_details)

        private val txtNoteLink = itemView.findViewById<TextView>(R.id.tv_note_link)
        private val ivNoteImage = itemView.findViewById<ImageView>(R.id.iv_note_image)

        private val txtNoteModificationDate = itemView.findViewById<TextView>(R.id.txt_note_modification_date)
        private val txtNoteModificationTime = itemView.findViewById<TextView>(R.id.txt_note_modification_time)

        private val ivSelected = itemView.findViewById<ImageView>(R.id.iv_selected)
        private val ivPinned = itemView.findViewById<ImageView>(R.id.iv_pinned)

        private val noteView = itemView.findViewById<CardView>(R.id.note_view)

        fun bind(position: Int, note: NoteEntity)
        {
            currentPosition = position
            currentNote = note

            with(currentNote) {
                txtNoteTitle.text = noteTitle
                txtNoteBody.text = noteBody

                txtNoteLink.apply {
                    text = noteAttachedLink
                    visibility = if (noteAttachedLink.isEmpty()) View.GONE else View.VISIBLE
                }

                ivNoteImage.apply {
                    visibility = if (noteImagePath.isEmpty()) View.GONE else View.VISIBLE
                    setImageURI(noteImagePath.toUri())
                }

                txtNoteModificationDate.text = modificationDate
                txtNoteModificationTime.text = modificationTime

                ivSelected.visibility = if (isSelected) View.VISIBLE else View.GONE
                ivPinned.visibility = if (isPinned) View.VISIBLE else View.GONE
            }
        }

        fun setListeners() {
            noteView.setOnLongClickListener()
            {
                notesAdapterInteractionListener?.onItemLongClicked()
                isMultiSelectEnabled = true
                currentNote.isSelected = true
                noteView.findViewById<ImageView>(R.id.iv_selected).visibility = View.VISIBLE
                if (!selectedNotes.contains(currentNote)) {
                    selectedNotes.add(currentNote)
                    notesAdapterInteractionListener?.onSelectedNotesChanged(selectedNotes)
                }
                true
            }
            noteView.setOnClickListener(this@NoteViewHolder)
        }

        override fun onClick(view: View?) {
            when(view?.id)
            {
                R.id.note_view -> {
                    if (!isMultiSelectEnabled)
                    {
                        val action = NotesFragmentDirections.actionNotesListToEditNote(currentNote)
                        noteView.findNavController().navigate(action)
                    }
                    else
                    {
                        if (!currentNote.isSelected) {
                            noteView?.apply {
                                ivSelected.visibility = View.VISIBLE
                                selectedNotes.add(currentNote)
                                notesAdapterInteractionListener?.onSelectedNotesChanged(selectedNotes)
                            }
                        }
                        else
                        {
                            noteView?.apply {
                                ivSelected.visibility = View.GONE
                            }
                            selectedNotes.remove(currentNote)
                            notesAdapterInteractionListener?.onSelectedNotesChanged(selectedNotes)
                        }

                        currentNote.isSelected = !currentNote.isSelected
                    }
                }
            }
        }
    }

    fun setMultiSelection(onOrOff: Boolean)
    {
        isMultiSelectEnabled = onOrOff
    }
    fun deleteNote(position: Int)
    {
        notes.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, notes.size)
    }

    fun addNotesAdapterInteractionListener(listener: NotesAdapterInteractionListener)
    {
        notesAdapterInteractionListener = listener
    }

    fun clearSelectedNotes()
    {
        selectedNotes.clear()
    }
    interface NotesAdapterInteractionListener
    {
        fun onItemLongClicked()
        fun onSelectedNotesChanged(selectedNotes: HashSet<NoteEntity>)
    }
}