package com.mohamedashraf.notes.ui.fragments.noteslist

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mohamedashraf.notes.NoteViewModel
import com.mohamedashraf.notes.R
import com.mohamedashraf.notes.database.NoteEntity
import com.mohamedashraf.notes.databinding.FragmentNotesListBinding
import com.mohamedashraf.notes.ui.fragments.FragmentToolbar
import com.mohamedashraf.notes.ui.fragments.ToolbarFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NotesFragment : ToolbarFragment() {

    private var _binding: FragmentNotesListBinding? = null
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var notesAdapter: NotesRecyclerAdapter
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var layoutManager: StaggeredGridLayoutManager

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesRecyclerView = binding.notesRV
        notesViewModel = ViewModelProvider(requireActivity()) [NoteViewModel::class.java]
        setupRecyclerView()

        notesAdapter.addNotesAdapterInteractionListener(object : NotesRecyclerAdapter.NotesAdapterInteractionListener
        {
            override fun onItemLongClicked() {
                notesViewModel.setActionMode(true)
            }

            override fun onSelectedNotesChanged(selectedNotes: HashSet<NoteEntity>) {
                notesViewModel.setSelectedNotes(selectedNotes)
            }
        })

        notesViewModel.allNotes.observe(viewLifecycleOwner)
        {
            notesAdapter.setList(it)
        }

        notesViewModel.searchResults.observe(viewLifecycleOwner)
        {
            notesAdapter.setList(it as ArrayList<NoteEntity>)
        }

        notesViewModel.sortedNotes.observe(viewLifecycleOwner)
        {
            notesAdapter.setList(it as ArrayList<NoteEntity>)
        }

        notesViewModel.getActionModeState().observe(viewLifecycleOwner) { isActionModeOn ->
            if (!isActionModeOn) {
                notesAdapter.isMultiSelectEnabled = false
                notesAdapter.clearSelectedNotes()
                resetNotesUIState()
            }
        }

        binding.btnAddNote.setOnClickListener {
            findNavController().navigate(R.id.action_NotesList_to_EditNote)
        }

        binding.searchViewNotes.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true;
            }

            override fun onQueryTextChange(queryString: String?): Boolean {
                queryString?.let {
                    val searchKey = "%${queryString}%"
                    notesViewModel.searchNotes(searchKey)
                    return true
                }
                return false
            }
        })
    }

    override fun buildFragmentToolbar(): FragmentToolbar {
        val menuItems = arrayListOf(R.id.action_sort, R.id.action_layout)
        val menuItemsClickListeners =
            arrayListOf<MenuItem.OnMenuItemClickListener?>(
                MenuItem.OnMenuItemClickListener {
                    showSortPopUpMenu()
                    true
                },
                MenuItem.OnMenuItemClickListener {

                    true
                }
            )

        return FragmentToolbar.Builder()
            .withId(binding.notesFragmentToolbar.toolbar.id)
            .withTitle(R.string.notes_fragment_label)
            .withMenu(R.menu.menu_notes_fragment)
            .withMenuItems(menuItems, menuItemsClickListeners)
            .build();
    }

    private fun showSortPopUpMenu()
    {
        val popMenu = PopupMenu(context, requireActivity().findViewById(R.id.action_sort))
        popMenu.menuInflater.inflate(R.menu.menu_sorting, popMenu.menu)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
            popMenu.setForceShowIcon(true)

        popMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId)
            {
                R.id.sort_by_title ->
                {
                    notesViewModel.sortNotesByTitle()
                    true
                }

                R.id.sort_by_creation_date ->
                {
                    notesViewModel.sortNotesByCreationDate()
                    true
                }
                else ->
                {
                    false
                }
            }
        }

        popMenu.show()
    }

    private fun setupRecyclerView()
    {
        layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        notesRecyclerView.layoutManager = layoutManager
        notesAdapter = NotesRecyclerAdapter(requireContext() /*, setSampleData()*/)
        notesRecyclerView.adapter = notesAdapter
    }

    private fun resetNotesUIState()
    {
        val size:Int = notesRecyclerView.adapter?.itemCount?: -1
        for (i in 0 until size) {
            notesRecyclerView.findViewHolderForAdapterPosition(i)?.itemView?.apply {
                isSelected = false
            }
        }
    }

    private fun setSampleData() : ArrayList<NoteEntity>
    {
        val date = getCurrentDate()
        val time = getCurrentTime()
        return arrayListOf(
            NoteEntity(noteTitle = "How to bypass the evil mango", noteBody =  "Bleeding mango guice skill", creationDate = date, creationTime = time),
            NoteEntity(noteTitle = "Note entity for room ", noteBody =  "Comparing two string literals fractions window", creationDate = date, creationTime = time),
            NoteEntity(noteTitle = "Setting option for cat", noteBody =  "Adapting the adapter to dog", creationDate = date, creationTime = time),
            NoteEntity(noteTitle = "Deleting items to my house", noteBody =  "How to compose the wild apple adapting the adapter", creationDate = date, creationTime = time),
            NoteEntity(noteTitle = "Adapting the adapter to dog", noteBody =  "Adapting the adapter to dog to compose the wild apple", creationDate = date, creationTime = time),
            NoteEntity(noteTitle = "Random early shitting", noteBody =  "Random early cats for comparing two string literals", creationDate = date, creationTime = time),
            NoteEntity(noteTitle = "How to compose the wild apple", noteBody =  "Deleting items to my house for cats to rain", creationDate = date, creationTime = time),
            NoteEntity(noteTitle = "Bleeding mango guice skill", noteBody =  "Random early cats nicks teams busy", creationDate = date, creationTime = time),
            NoteEntity(noteTitle = "Mavericks to nicks teams busy", noteBody =  "nicks teams busy counter poops", creationDate = date, creationTime = time),
            NoteEntity(noteTitle = "Comparing two string literals", noteBody =  "Mavericks to nicks teams busy man women", creationDate = date, creationTime = time),
            NoteEntity(noteTitle = "Hello idiots counter poops", noteBody =  "Deleting items to my house by early cats", creationDate = date, creationTime = time),
        )
    }

    private fun getCurrentDate() : String
    {
        val sdf = SimpleDateFormat("d MMMM, y", Locale.getDefault());
        return sdf.format(Date())
    }

    private fun getCurrentTime(locale: Locale = Locale.getDefault()) : String
    {
        val sdf = SimpleDateFormat("hh:mm a", locale);
        return sdf.format(Date())
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}