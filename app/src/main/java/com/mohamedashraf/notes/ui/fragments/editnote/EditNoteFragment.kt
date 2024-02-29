package com.mohamedashraf.notes.ui.fragments.editnote

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mohamedashraf.notes.NoteViewModel
import com.mohamedashraf.notes.R
import com.mohamedashraf.notes.database.NoteEntity
import com.mohamedashraf.notes.databinding.CustomDialogBinding
import com.mohamedashraf.notes.databinding.FragmentEditNoteBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class EditNoteFragment : Fragment() {

    private val args by navArgs<EditNoteFragmentArgs>()
    private lateinit var editNoteViewModel: EditNoteViewModel
    private lateinit var noteViewModel: NoteViewModel
    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var alertDialog: AlertDialog

    private lateinit var dialogBinding: CustomDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editNoteViewModel = ViewModelProvider(requireActivity())[EditNoteViewModel::class.java]
        noteViewModel = ViewModelProvider(requireActivity())[NoteViewModel::class.java]

        dialogBinding = CustomDialogBinding.inflate(layoutInflater, null, false)

        alertDialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root).create()
        alertDialog.window?.apply {
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialogBinding.btnCancel.setOnClickListener()
        {
            alertDialog.dismiss()
        }

        if (args.note != null) // existing note
        {
            binding.edNoteTitle.editText?.setText(args.note?.noteTitle)
            binding.edNoteDetails.editText?.setText(args.note?.noteBody)
            binding.tvDate.text = args.note?.creationDate
            binding.tvTime.text = args.note?.creationTime
            updateCharsCnt()
        } else // new note
        {
            binding.tvDate.text = getCurrentDate()
            binding.tvTime.text = getCurrentTime()
        }

        binding.edNoteTitle.editText?.addTextChangedListener {
            editNoteViewModel.setNoteTitle(it.toString())
            updateCharsCnt()

        }

        binding.edNoteDetails.editText?.addTextChangedListener()
        {
            editNoteViewModel.setNoteDetails(it.toString())
            updateCharsCnt()
        }

        binding.tvCharsCounter.addTextChangedListener()
        {
            editNoteViewModel.setCharsCounter(it?.length ?: 0)
        }

        binding.btnAddImage.setOnClickListener()
        {
            alertDialog.show()
        }

        editNoteViewModel.getNoteTitle().observe(viewLifecycleOwner)
        {
            //binding.edNoteTitle.editText?.text = it.toString()
            //Toast.makeText(requireContext(), "Title Changed", Toast.LENGTH_SHORT).show()
        }
        editNoteViewModel.getNoteDetails().observe(viewLifecycleOwner)
        {
            //binding.edNoteDetails.editText?.setText(it.toString())
            //Toast.makeText(requireContext(), "Details Changed", Toast.LENGTH_SHORT).show()
        }

        editNoteViewModel.getCharsCounter().observe(viewLifecycleOwner)
        {
            //binding.tvCharsCounter.text = "$it ${getString(R.string.characters)}"
            //Toast.makeText(requireContext(), "Chars Changed", Toast.LENGTH_SHORT).show()
        }

        binding.btAddFinish.setOnClickListener {

            val note = NoteEntity(
                noteTitle = binding.edNoteTitle.editText?.text.toString(),
                noteBody = binding.edNoteDetails.editText?.text.toString(),
                creationDate = binding.tvDate.text.toString(),
                creationTime = binding.tvTime.text.toString()
            )


            if (args.note == null) {
                noteViewModel.addNoteToDataBase(note)
            } else {
                note.noteId = args.note?.noteId!!
                noteViewModel.updateNote(note)
            }

            findNavController().navigate(R.id.action_EditNoteFragment_to_NotesFragment)
        }

    }

    private fun updateCharsCnt()
    {
        val cnt = (binding.edNoteTitle.editText?.text?.length?: 0)  +
                  (binding.edNoteDetails.editText?.text?.length?: 0)

        binding.tvCharsCounter.text = "${cnt.toString()} Characters"
    }

    private fun getCurrentDate() : String
    {
        return SimpleDateFormat("d MMMM, y", Locale.getDefault()).format(Date())
    }

    private fun getCurrentTime(locale: Locale = Locale.getDefault()) : String
    {
        return SimpleDateFormat("hh:mm a", locale).format(Date())
    }

}