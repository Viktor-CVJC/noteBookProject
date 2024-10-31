package com.example.notebookproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
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
    val timestamp: Date = Date(),
)

class NoteBookViewModel : ViewModel() {
    private val _notes = mutableStateListOf<Note>()
    val notes: List<Note> = _notes

    fun addNote(title: String, text: String) {
        _notes.add(0, Note(title = title, text = text, timestamp = Date()))
    }

    fun updateNote(index: Int, title: String, text: String) {
        if (index in _notes.indices) {
            val existingNote = _notes[index]
            _notes[index] = existingNote.copy(title = title, text = text, timestamp = existingNote.timestamp)
        }
    }
    fun deleteNote(index: Int) {
        if (index in _notes.indices) {
            _notes.removeAt(index)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteBookProjectTheme {
                val navController = rememberNavController()
                val viewModel: NoteBookViewModel = viewModel()
                NoteBookNavHost(
                    navController = navController,
                    startDestination = "overview",
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun NoteBookNavHost(navController: NavHostController, startDestination: String, viewModel: NoteBookViewModel) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("overview") { OverviewScreen(navController, viewModel) }
        composable("detail/{noteIndex}") { backStackEntry ->
            val noteIndex = backStackEntry.arguments?.getString("noteIndex")?.toIntOrNull() ?: 0
            DetailScreen(navController, viewModel, noteIndex)
        }
        composable("create") { CreateEditScreen(navController, viewModel) }
        composable("edit/{noteIndex}") { backStackEntry ->
            val noteIndex = backStackEntry.arguments?.getString("noteIndex")?.toIntOrNull()
            CreateEditScreen(navController, viewModel, noteIndex)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteTopBar(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(navController: NavHostController, viewModel: NoteBookViewModel) {
    val notes by remember { mutableStateOf(viewModel.notes) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                actions = {
                    IconButton(onClick = { navController.navigate("create") }) {
                        Icon(Icons.Default.Add, "Add note")
                    }
                }
            )
        }
    ) { padding ->
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No notes yet. Tap + to create one.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                itemsIndexed(notes) { index, note ->
                    NoteItem(
                        note = note,
                        onClick = { navController.navigate("detail/$index") }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateFormat.format(note.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun DetailScreen(navController: NavHostController, viewModel: NoteBookViewModel, noteIndex: Int) {
    val note = viewModel.notes.getOrNull(noteIndex)

    Scaffold(
        topBar = {
            NoteTopBar(
                title = note?.title ?: "Note Details",
                showBackButton = true,
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { contentPadding ->
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
fun CreateEditScreen(navController: NavHostController, viewModel: NoteBookViewModel, noteIndex: Int? = null) {
    val isEditing = noteIndex != null
    val note = if (isEditing) viewModel.notes.getOrNull(noteIndex!!) else null

    var title by remember { mutableStateOf(note?.title ?: "") }
    var text by remember { mutableStateOf(note?.text ?: "") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var textError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            NoteTopBar(
                title = if (isEditing) "Edit Note" else "Create Note",
                showBackButton = true,
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { contentPadding ->
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
                                noteIndex!!,
                                title,
                                text
                            )
                        } else {
                            viewModel.addNote(
                                title,
                                text
                            )
                        }
                        navController.popBackStack()
                    }
                },
                enabled = titleError == null && textError == null && title.isNotEmpty(),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(if (isEditing) "Update" else "Create")
            }
        }
    }
}