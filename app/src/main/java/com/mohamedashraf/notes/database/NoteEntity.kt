package com.mohamedashraf.notes.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "note_entity")
data class NoteEntity (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    var noteId: Long = 0,

    @ColumnInfo(name = "note_title")
    var noteTitle: String = "",

    @ColumnInfo(name = "note_body")
    var noteBody: String = "",

    @ColumnInfo(name = "image_path")
    var noteImagePath: String = "",

    @ColumnInfo(name = "attached_link")
    var noteAttachedLink: String = "",

    @ColumnInfo(name = "creation_date")
    val creationDate: String,

    @ColumnInfo(name = "creation_time")
    val creationTime: String,

    @ColumnInfo(name = "is_pinned")
    var isPinned: Boolean = false,

) : Parcelable{
    @IgnoredOnParcel @Ignore
    var isSelected: Boolean = false
}
