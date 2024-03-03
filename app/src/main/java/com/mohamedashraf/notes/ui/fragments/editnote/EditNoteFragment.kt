package com.mohamedashraf.notes.ui.fragments.editnote

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mohamedashraf.notes.NoteViewModel
import com.mohamedashraf.notes.R
import com.mohamedashraf.notes.database.NoteEntity
import com.mohamedashraf.notes.databinding.AddLinkDialogBinding
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

    private lateinit var addLinkDialog: AlertDialog
    private lateinit var addLinkDialogBinding: AddLinkDialogBinding

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)

        registerForGalleryResult()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editNoteViewModel = ViewModelProvider(requireActivity())[EditNoteViewModel::class.java]
        noteViewModel = ViewModelProvider(requireActivity())[NoteViewModel::class.java]

        dialogBinding = CustomDialogBinding.inflate(layoutInflater, null, false)
        addLinkDialogBinding = AddLinkDialogBinding.inflate(layoutInflater, null, false)

        alertDialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root).create()
        alertDialog.window?.apply {
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        addLinkDialog = AlertDialog.Builder(requireContext()).setView(addLinkDialogBinding.root).create()
        addLinkDialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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

        binding.btnAttachLink.setOnClickListener()
        {
            addLinkDialog.show()
        }

        binding.tvNoteAttachedLink.setOnClickListener()
        {
            try {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(binding.tvNoteAttachedLink.text.toString())))
            }catch (_: Exception)
            {
                Toast.makeText(requireContext(), "Invalid link.", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.btnGallery.setOnClickListener()
        {
            pickImageLauncher.launch("image/*")
            alertDialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener()
        {
            alertDialog.dismiss()
        }


        addLinkDialogBinding.btnOk.setOnClickListener {
            val linkText = addLinkDialogBinding.etLink.text.toString()
            val underlinedText = SpannableString(linkText).apply {
                setSpan(UnderlineSpan(), 0, length, 0)
            }
            binding.tvNoteAttachedLink.text = underlinedText
            addLinkDialog.dismiss()
        }


        addLinkDialogBinding.btnCancel.setOnClickListener()
        {
            addLinkDialog.dismiss()
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

    private fun registerForGalleryResult()
    {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            binding.ivNoteImage.setImageURI(it)
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