package com.mohamedashraf.notes.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "note_entity")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    val noteId:Long = 0,
    @ColumnInfo(name = "note_title") var noteTitle:String = "",
    @ColumnInfo(name = "note_body") var noteBody:String = "",
    @ColumnInfo(name = "note_date") val creationDate:String,
    @ColumnInfo(name = "note_time") val creationTime:String
)
