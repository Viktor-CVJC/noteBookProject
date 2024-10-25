package com.example.notebookproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
        composable("detail") { DetailScreen(navController) }
        composable("create") { CreateEditScreen(navController) }
    }
}

@Composable
fun OverviewScreen(navController: NavHostController) { /* going to implement */ }

@Composable
fun DetailScreen(navController: NavHostController) { /* going to implement */ }

@Composable
fun CreateEditScreen(navController: NavHostController) { /* going to implement */ }