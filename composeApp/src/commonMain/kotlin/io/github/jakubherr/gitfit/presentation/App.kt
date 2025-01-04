package io.github.jakubherr.gitfit.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            // TODO add application-level UI state holder

            val navController = rememberNavController()
            GitFitNavHost(navController)
        }
    }
}
