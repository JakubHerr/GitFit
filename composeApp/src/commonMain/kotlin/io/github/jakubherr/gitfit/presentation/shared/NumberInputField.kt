package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun NumberInputField(
    value: String,
    label: String? = null,
    modifier: Modifier = Modifier.width(64.dp),
    placeholder: Int = 0,
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value,
        onValueChange,
        modifier = modifier,
        placeholder = { Text(placeholder.toString(), Modifier.alpha(0.6f)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isError,
        singleLine = true,
    )
}