package com.mohamedashraf.notes.ui.fragments.editnote

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mohamedashraf.notes.NoteViewModel
import com.mohamedashraf.notes.R
import com.mohamedashraf.notes.database.NoteEntity
import com.mohamedashraf.notes.databinding.AddImageDialogBinding
import com.mohamedashraf.notes.databinding.AddLinkDialogBinding
import com.mohamedashraf.notes.databinding.FragmentEditNoteBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class EditNoteFragment : Fragment() {

    private val args by navArgs<EditNoteFragmentArgs>()
    private lateinit var editNoteViewModel: EditNoteViewModel
    private lateinit var noteViewModel: NoteViewModel
    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var addImageDialog: AlertDialog
    private lateinit var addImageDialogBinding: AddImageDialogBinding

    private lateinit var addLinkDialog: AlertDialog
    private lateinit var addLinkDialogBinding: AddLinkDialogBinding

    private lateinit var requestGalleryPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takeImageLauncher: ActivityResultLauncher<Intent>

    private var photoFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerPermissions()
        registerForGalleryResult()
        registerForCameraResult()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editNoteViewModel = ViewModelProvider(requireActivity())[EditNoteViewModel::class.java]
        noteViewModel = ViewModelProvider(requireActivity())[NoteViewModel::class.java]

        addImageDialogBinding = AddImageDialogBinding.inflate(layoutInflater, null, false)
        addLinkDialogBinding = AddLinkDialogBinding.inflate(layoutInflater, null, false)

        addImageDialog = AlertDialog.Builder(requireContext()).setView(addImageDialogBinding.root).create()
        addImageDialog.window?.apply {
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        addLinkDialog = AlertDialog.Builder(requireContext()).setView(addLinkDialogBinding.root).create()
        addLinkDialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }


        args.note?.let { //existing note
            binding.edNoteTitle.editText?.setText(it.noteTitle)
            binding.edNoteDetails.editText?.setText(it.noteBody)

            populateNoteImage(it.noteImagePath.toUri())

            displayAttachedLink(it.noteAttachedLink)

            binding.tvDate.text = it.creationDate
            binding.tvTime.text = it.creationTime
            updateCharsCnt()

        } ?: run {//new note
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
            addImageDialog.show()
        }

        binding.btnAttachLink.setOnClickListener()
        {
            addLinkDialog.show()
        }

        binding.tvNoteAttachedLink.setOnClickListener()
        {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(binding.tvNoteAttachedLink.text.toString())
                    )
                )
            } catch (_: Exception) {
                Toast.makeText(requireContext(), "Invalid link.", Toast.LENGTH_SHORT).show()
            }
        }

        addImageDialogBinding.btnGallery.setOnClickListener()
        {
            checkGalleryPermission()
            addImageDialog.dismiss()
        }

        addImageDialogBinding.btnCamera.setOnClickListener()
        {
            checkCameraPermission()
            addImageDialog.dismiss()
        }

        addImageDialogBinding.btnCancel.setOnClickListener()
        {
            addImageDialog.dismiss()
        }


        addLinkDialogBinding.btnOk.setOnClickListener {
            displayAttachedLink( addLinkDialogBinding.etLink.text.toString())
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
            var imagePath = ""
            binding.ivNoteImage.tag?.let {
                imagePath = it.toString()
            }

            val note = NoteEntity(
                noteTitle = binding.edNoteTitle.editText?.text.toString(),
                noteBody = binding.edNoteDetails.editText?.text.toString(),
                noteImagePath = imagePath,
                noteAttachedLink = binding.tvNoteAttachedLink.text.toString(),
                creationDate = binding.tvDate.text.toString(),
                creationTime = binding.tvTime.text.toString(),
            )

            args.note?.let {
                note.noteId = args.note?.noteId!!
                note.isPinned = args.note?.isPinned!!
                noteViewModel.updateNote(note)
            } ?: run {
                noteViewModel.addNoteToDataBase(note)
            }

            findNavController().navigate(R.id.action_EditNoteFragment_to_NotesFragment)
        }

    }

    private fun displayAttachedLink(linkText: String)
    {
        if (linkText.isNotBlank())
        {
            val underlinedText = SpannableString(linkText)
            underlinedText.setSpan(UnderlineSpan(), 0, linkText.length, 0)

            binding.tvNoteAttachedLink.apply {
                text = underlinedText
                visibility = View.VISIBLE
            }
        }
    }
    private fun registerPermissions() {
        requestGalleryPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                pickImage()
            }
        }

        requestCameraPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                takePhoto()
            }
        }
    }

    private fun registerForGalleryResult() {
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it.resultCode == Activity.RESULT_OK) {

                    val selectedImageUri: Uri? = it.data?.data

                    requireActivity().grantUriPermission(requireActivity().packageName, selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    selectedImageUri?.run {
                        val takeFlags: Int = it?.data?.flags?.and(Intent.FLAG_GRANT_READ_URI_PERMISSION) ?: 0
                        requireActivity().contentResolver.takePersistableUriPermission(selectedImageUri, takeFlags)
                    }

                    populateNoteImage(selectedImageUri)
                }
            }
    }

    private fun registerForCameraResult() {
        takeImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it.resultCode == Activity.RESULT_OK) {
                    photoFile?.let { file ->
                        val imageUri: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.mohamedashraf.notes.fileProvider",
                            file
                        )
                        populateNoteImage(imageUri)
                    }
                }
            }
    }

    private fun populateNoteImage(imageUri: Uri?)
    {
        if (imageUri?.path?.isNotBlank() == true)
        {
            binding.ivNoteImage.apply {
                visibility = View.VISIBLE
                tag = imageUri
                setImageURI(imageUri)
            }
        }
    }

    private fun checkCameraPermission() {
        val cameraPermission = Manifest.permission.CAMERA
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                cameraPermission
            ) == PackageManager.PERMISSION_GRANTED -> {
                takePhoto()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                cameraPermission
            ) -> {
                requestCameraPermissionLauncher.launch(cameraPermission)
            }

            else -> {
                requestCameraPermissionLauncher.launch(cameraPermission)
            }
        }
    }

    private fun checkGalleryPermission() {
        val galleryPermission = Manifest.permission.READ_EXTERNAL_STORAGE
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                galleryPermission
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickImage()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                galleryPermission
            ) -> {
                requestGalleryPermissionLauncher.launch(galleryPermission)
            }

            else -> {
                requestGalleryPermissionLauncher.launch(galleryPermission)
            }
        }
    }

    private fun pickImage()
    {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun takePhoto()
    {
        photoFile = createImageFile()
        photoFile?.let { file ->
            val photoUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.mohamedashraf.notes.fileProvider",
                file
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            takeImageLauncher.launch(intent)
        }
    }

    private fun createImageFile(): File? {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
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