package com.mohamedashraf.notes.ui.activities

import android.os.Bundle
import android.view.ActionMode
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.mohamedashraf.notes.NoteViewModel
import com.mohamedashraf.notes.R
import com.mohamedashraf.notes.database.NoteEntity
import com.mohamedashraf.notes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){

    private val TAG = "MainActivity"
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var actionModeCallBack: ActionMode.Callback
    private var actionMode: ActionMode? = null

    private var selectedNotes: HashSet<NoteEntity> = HashSet()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        /*appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)*/

        notesViewModel = ViewModelProvider(this) [NoteViewModel::class.java]

        notesViewModel.getActionModeState().observe(this) { isActionModeOn ->
            if (isActionModeOn)
                actionMode = startActionMode(actionModeCallBack)
        }

        notesViewModel.getSelectedNotes().observe(this) {
            actionMode?.title =("${it.size}")
            selectedNotes = it
        }

        actionModeCallBack =  object: ActionMode.Callback{
            override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
                actionMode?.menuInflater?.inflate(R.menu.contextual_menu, menu)
                return true
            }

            override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(p0: ActionMode?, item: MenuItem?): Boolean {
                when(item?.itemId)
                {
                    R.id.action_delete -> {
                        deleteSelectedNotes()
                    }
                    R.id.action_pin -> {
                        pinSelectedNotes()
                    }
                }
                actionMode?.finish()
                return true
            }

            override fun onDestroyActionMode(p0: ActionMode?) {
                notesViewModel.setActionMode(false)
                actionMode = null
            }
        }
    }

    private fun deleteSelectedNotes()
    {
        selectedNotes.forEach { note ->
            notesViewModel.deleteNoteById(note.noteId)
        }
    }
    private fun pinSelectedNotes()
    {
        selectedNotes.forEach { note ->
            notesViewModel.pinNoteById(note.noteId)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}