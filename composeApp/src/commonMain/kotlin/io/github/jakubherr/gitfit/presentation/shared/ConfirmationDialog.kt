package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun ConfirmationDialog(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    confirmText: String,
    onConfirm: () -> Unit = {},
    dismissText: String,
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        icon = { Icon(Icons.Default.Warning, null) },
        title = { Text(title) },
        modifier = Modifier.testTag("ConfirmationDialog"),
        text = { Text(text) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onConfirm,
                modifier = Modifier.testTag("ConfirmDialogButton")
            ) {
                Text(confirmText, color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(
                onDismiss,
                modifier = Modifier.testTag("DismissDialogButton")
            ) {
                Text(dismissText)
            }
        },
    )
}
