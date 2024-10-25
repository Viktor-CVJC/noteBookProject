package com.example.notebookproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notebookproject.ui.theme.NoteBookProjectTheme
import androidx.compose.foundation.layout.padding

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
        composable("detail") { DetailScreen(navController) }
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
fun DetailScreen(navController: NavHostController) { /* going to implement */ }

@Composable
fun CreateEditScreen(navController: NavHostController) { /* going to implement */ }