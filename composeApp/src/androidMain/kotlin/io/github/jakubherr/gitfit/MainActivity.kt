package io.github.jakubherr.gitfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.FirebaseApp
import io.github.jakubherr.gitfit.presentation.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        // TODO fix window insets when app is in landscape mode
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        setContent {
            App()
        }
    }
}
