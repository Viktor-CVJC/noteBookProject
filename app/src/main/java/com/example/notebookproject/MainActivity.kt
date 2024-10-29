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

    var titleError by remember { mutableStateOf<String?>(null) }
    var textError by remember { mutableStateOf<String?>(null) }

    Scaffold { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            TextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = when {
                        title.length < 3 -> "Title must be at least 3 characters"
                        title.length > 50 -> "Title must be at most 50 characters"
                        else -> null
                    }
                },
                label = { Text("Title") },
                isError = titleError != null
            )
            titleError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            TextField(
                value = text,
                onValueChange = {
                    text = it
                    textError =
                        if (text.length > 120) "Text can't be more than 120 characters." else null
                },
                label = { Text("Text") },
                isError = textError != null
            )
            textError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    if (titleError == null && textError == null) {
                        if (isEditing) {
                            viewModel.updateNote(
                                index = noteIndex!!,
                                title = title,
                                text = text
                            )
                        } else {
                            viewModel.addNote(
                                title = title,
                                text = text
                            )
                        }
                        navController.navigateUp()
                    }
                },
                enabled = titleError == null && textError == null
            ) {
                Text(if (isEditing) "Update" else "Create")
            }
        }
    }
}