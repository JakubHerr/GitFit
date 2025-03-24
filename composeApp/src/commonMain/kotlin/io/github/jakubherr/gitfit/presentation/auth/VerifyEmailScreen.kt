package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VerifyEmailScreenRoot(
    vm: AuthViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onSkip: () -> Unit = {},
) {
    LaunchedEffect(null) {
        while (true) {
            delay(5_000L)
            println("DBG: Checking for email verification...")
            if (vm.currentUser.emailVerified) break
        }
        onSkip()
    }

    Column(modifier.fillMaxSize()) {
        Text("Your email is not verified. If you forget your password, you might be unable to delete your account")

        // TODO rate limit in UI
        Button({ vm.onAction(AuthAction.VerifyEmail)}) {
            Text("Send verification email")
        }

        Button(onSkip) {
            Text("Skip verification")
        }
    }
}