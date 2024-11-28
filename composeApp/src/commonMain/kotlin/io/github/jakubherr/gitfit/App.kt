package io.github.jakubherr.gitfit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.mx
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val auth = Firebase.auth // TODO fix desktop implementation

    MaterialTheme {
        val scope = rememberCoroutineScope()
        var showContent by remember { mutableStateOf(false) }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }


        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString().substringBefore("T")
                    Text("Compose: $greeting, $time")
                    Image(painterResource(Res.drawable.mx), "")

                    TextField(email, onValueChange = { email = it})
                    TextField(password, onValueChange = { password = it}, visualTransformation = PasswordVisualTransformation())
                    Button(onClick = {
                        scope.launch {
                            auth.createUserWithEmailAndPassword(email, password)
                        }
                    }) {
                        Text("register")
                    }
                    Button(onClick = { }) {
                        Text("Google")
                    }
                }

            }
        }
    }
}