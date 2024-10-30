package com.example.notebookproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.icons.filled.ArrowBack
import androidx.compose.material3.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*
import com.example.notebookproject.ui.theme.NoteBookProjectTheme


data class Note(
    val title: String,
    val text: String,
    val timestamp: Date = Date()
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteBookProjectTheme {
                val navController = rememberNavController()
                NoteBookNavHost(navController = navController, startDestination = "overview") {
                    composable("overview") {
                        OverviewScreen(navController)
                    }
                    composable("detail/{noteIndex}") { backStackEntry ->
                        val noteIndex =
                            backStackEntry.arguments?.getString("noteIndex")?.toIntOrNull() ?: 0
                        DetailScreen(navController, noteIndex)
                    }
                    composable("create") {
                        CreateEditScreen(navController)
                    }
                    composable("edit/{noteIndex}") { backStackEntry ->
                        val noteIndex =
                            backStackEntry.arguments?.getString("noteIndex")?.toIntOrNull() ?: 0
                        CreateEditScreen(navController, noteIndex)
                    }
                }
            }
        }
    }
}

@Composable
fun NoteBookNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "overview") {
        composable("overview") { OverviewScreen(navController) }
        composable("detail/{noteIndex}") { backStackEntry ->
            val noteIndex = backStackEntry.arguments?.getString("noteIndex")?.toIntOrNull() ?: 0
            DetailScreen(navController, noteIndex = noteIndex)
        }
        composable("create") { CreateEditScreen(navController) }
        composable("edit/{noteIndex}") { backStackEntry ->
            val noteIndex = backStackEntry.arguments?.getString("noteIndex")?.toIntOrNull()
            CreateEditScreen(navController, noteIndex = noteIndex)
        }
    }
}

@Composable
fun OverviewScreen(navController: NavHostController) {
    val viewModel: NoteBookViewModel = viewModel()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create") }) {
                Text("Add note")
            }
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.padding(contentPadding)
        ) {
            itemsIndexed(viewModel.notes) { index, note ->
                Text(
                    text = note.title,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("detail/$index")
                        }
                        .padding(16.dp)
                )
            }
        }
    }
}
@Composable
fun DetailScreen(navController: NavHostController, noteIndex: Int) {
    val viewModel: NoteBookViewModel = viewModel()
    val note = viewModel.notes.getOrNull(noteIndex)

    Scaffold { contentPadding ->
        if (note != null) {
            Column(modifier = Modifier.padding(contentPadding)) {
                Text(text = note.title)
                Text(text = note.text)

                Button( onClick = {
                    navController.navigate("edit/$noteIndex")
                }) {
                    Text("Edit")
                }

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
fun CreateEditScreen(navController: NavHostController, noteIndex: Int? = null) {
    val viewModel: NoteBookViewModel = viewModel()
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