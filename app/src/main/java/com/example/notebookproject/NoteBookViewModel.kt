package com.example.notebookproject

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class NoteBookViewModel : ViewModel() {
    private val _notes = mutableStateListOf<Note>()
    val notes: List<Note> = _notes

    fun addNote(title: String, text: String) {
        _notes.add(Note(title, text))
    }

    fun updateNote(index: Int, title: String, text: String) {
        if (index in _notes.indices) {
            _notes[index] = Note(title, text)
        }
    }
    fun deleteNote(index: Int) {
        if (index in _notes.indices)
            _notes.removeAt(index)
    }
}

