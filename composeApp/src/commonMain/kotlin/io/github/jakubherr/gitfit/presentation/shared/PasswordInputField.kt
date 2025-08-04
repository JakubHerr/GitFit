package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.password
import gitfit.composeapp.generated.resources.show_password
import org.jetbrains.compose.resources.stringResource

@Composable
fun PasswordInputField(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(Res.string.password),
    imeAction: ImeAction = ImeAction.Done,
) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        password,
        onValueChange = { onPasswordChange(it) },
        modifier = modifier,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        trailingIcon = {
            IconButton({ showPassword = !showPassword }) {
                Icon(
                    if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    stringResource(Res.string.show_password),
                )
            }
        },
        label = { Text(label) },
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction,
            ),
    )
}
