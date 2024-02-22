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

    @Query("SELECT * FROM note_entity")
    fun getAllNotes() : LiveData<List<NoteEntity>>

    @Query("SELECT * FROM note_entity")
    suspend fun readAll() : List<NoteEntity>

    @Query("SELECT * FROM note_entity WHERE note_id = :noteId")
    suspend fun getNoteById(noteId: Long): NoteEntity?
    @Query("SELECT * FROM note_entity WHERE note_title LIKE :searchKey")
    suspend fun searchNotes(searchKey: String): List<NoteEntity>

}