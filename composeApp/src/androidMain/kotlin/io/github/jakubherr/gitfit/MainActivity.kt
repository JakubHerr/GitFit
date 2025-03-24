package io.github.jakubherr.gitfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.FirebaseApp
import io.github.jakubherr.gitfit.domain.AuthError
import io.github.jakubherr.gitfit.domain.model.User
import io.github.jakubherr.gitfit.presentation.App
import io.github.jakubherr.gitfit.presentation.auth.AuthState
import io.github.jakubherr.gitfit.presentation.auth.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun CommonPreview() {
    MaterialTheme {
        LoginScreen(AuthState(User.LoggedOut, AuthError.PasswordTooWeak, true))
    }
}
