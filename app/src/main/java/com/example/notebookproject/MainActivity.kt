package com.example.notebookproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notebookproject.ui.theme.NoteBookProjectTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteBookProjectTheme {
                val navController = rememberNavController()
                NoteBookNavHost(navController = navController)
            }
        }
    }
}

@Composable
fun NoteBookNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "overview") {
        composable("overview") { OverviewScreen(navController) }
        composable("detail") { DetailScreen(navController, noteIndex = 0) }
        composable("create") { CreateEditScreen(navController) }
    }
}

@Composable
fun OverviewScreen(navController: NavHostController, viewModel: NoteBookViewModel = NoteBookViewModel()) {
    Scaffold { contentPadding ->
        LazyColumn(
            modifier = Modifier.padding(contentPadding)
        ) {
            items(viewModel.notes) { note ->
                Text(text = note.title)
            }
        }
    }
}
@Composable
fun DetailScreen(navController: NavHostController, viewModel: NoteBookViewModel = NoteBookViewModel(), noteIndex: Int) {
    val note = viewModel.notes.getOrNull(noteIndex)

    Scaffold { contentPadding ->
        if (note != null) {
            Column(modifier = Modifier.padding(contentPadding)) {
                Text(text = note.title)
                Text(text = note.text)
                Button(onClick = {
                    viewModel.deleteNote(noteIndex)
                    navController.navigateUp()
                }) {
                    Text("Delete")
                }
            }
        } else {
            Text("Note not found")
        }
    }
}

@Composable
fun CreateEditScreen(navController: NavHostController, viewModel: NoteBookViewModel = NoteBookViewModel(), noteIndex: Int? = null) {
    val isEditing = noteIndex != null
    val note = if (isEditing) viewModel.notes.getOrNull(noteIndex!!) else null

    var title by remember { mutableStateOf(note?.title ?: "") }
    var text by remember { mutableStateOf(note?.text ?: "") }

    Scaffold { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") }
            )
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Text") }
            )
            Button(onClick = {
                if (isEditing) {
                    viewModel.updateNote(noteIndex!!, title, text)
                } else {
                    viewModel.addNote(title, text)
                }
                navController.navigateUp()
            }) {
                Text(if (isEditing) "Update" else "Create")
            }
        }
    }
}