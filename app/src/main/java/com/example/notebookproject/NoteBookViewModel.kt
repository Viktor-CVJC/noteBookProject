package com.example.notebookproject

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.Date

class NoteBookViewModel : ViewModel() {
    private val _notes = mutableStateListOf<Note>()
    val notes: List<Note> = _notes

    fun addNote(title: String, text: String) {
        _notes.add(Note(title = title, text = text, timestamp = Date()))
    }

    fun updateNote(index: Int, title: String, text: String) {
        if (index in _notes.indices) {
            val existingNote = _notes[index]
            _notes[index] = Note(title = title, text = text, timestamp = existingNote.timestamp)
        }
    }
    fun deleteNote(index: Int) {
        if (index in _notes.indices) {
            _notes.removeAt(index)
        }
    }
}

