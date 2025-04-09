package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StringInputField(
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int,
    modifier: Modifier = Modifier,
    isError: Boolean = value.isBlank(),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
) {
    TextField(
        value,
        onValueChange = { if (it.length <= maxLength) onValueChange(it) },
        modifier,
        singleLine = true,
        label = label,
        isError = isError,
        placeholder = placeholder,
    )
}
