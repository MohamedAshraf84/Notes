package com.mohamedashraf.notes.ui.fragments.editnote

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.mohamedashraf.notes.databinding.FragmentEditNoteBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class EditNoteFragment : Fragment() {

    private lateinit var editNoteViewModel  : EditNoteViewModel
    private var         _binding            : FragmentEditNoteBinding? = null
    private val binding get() = _binding!!

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

        binding.edNoteTitle.editText?.addTextChangedListener {
            editNoteViewModel.updateNoteTitle(it.toString())
        }
        binding.edNoteDetails.editText?.addTextChangedListener()
        {
            editNoteViewModel.updateNoteDetails(it.toString())
        }

        editNoteViewModel.getNoteTitle().observe(viewLifecycleOwner)
        {
            //binding.edNoteTitle.editText?.text = it.toString()
            Toast.makeText(requireContext(), "Title Changed", Toast.LENGTH_SHORT).show()
        }
        editNoteViewModel.getNoteDetails().observe(viewLifecycleOwner)
        {
            //binding.edNoteDetails.editText?.setText(it.toString())
            Toast.makeText(requireContext(), "Details Changed", Toast.LENGTH_SHORT).show()
        }

        editNoteViewModel.getCharsCounter().observe(viewLifecycleOwner)
        {
            binding.tvCharsCounter.text = "${it.toString()} Characters"
            //Toast.makeText(requireContext(), "Chars Changed", Toast.LENGTH_SHORT).show()
        }

        binding.btAddFinish.setOnClickListener{

            findNavController().navigateUp()
        }
    }


    override fun onStart() {
        super.onStart()
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