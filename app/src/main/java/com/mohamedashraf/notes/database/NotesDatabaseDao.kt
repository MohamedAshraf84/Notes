package com.mohamedashraf.notes.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("DELETE FROM note_entity WHERE note_id = :noteId")
    suspend fun deleteNoteById(noteId: Long)

    @Update
    suspend fun update(note: NoteEntity)

    @Query("SELECT * FROM note_entity ORDER BY is_pinned DESC, note_id ASC")
    fun getAllNotes() : LiveData<List<NoteEntity>>

    @Query("SELECT * FROM note_entity WHERE note_id = :noteId")
    suspend fun getNoteById(noteId: Long): NoteEntity?

    @Query("SELECT * FROM note_entity WHERE note_title LIKE :searchKey")
    suspend fun searchNotes(searchKey: String): List<NoteEntity>

    @Query("SELECT * FROM note_entity ORDER BY note_title, note_id ASC")
    suspend fun sortNotesByTitle(): List<NoteEntity>

    @Query("SELECT * FROM note_entity ORDER BY creation_date DESC, creation_time DESC")
    suspend fun sortNotesByCreationDate(): List<NoteEntity>

    @Query("SELECT * FROM note_entity ORDER BY modification_date DESC, modification_time DESC")
    suspend fun sortNotesByModificationDate(): List<NoteEntity>
}