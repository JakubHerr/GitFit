package io.github.jakubherr.gitfit.presentation.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.cancel
import gitfit.composeapp.generated.resources.delete_workout
import gitfit.composeapp.generated.resources.delete_workout_explanation
import gitfit.composeapp.generated.resources.delete_workout_record
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.presentation.shared.ConfirmationDialog
import io.github.jakubherr.gitfit.presentation.shared.WorkoutBlockItem
import org.jetbrains.compose.resources.stringResource

@Composable
fun WorkoutDetailScreen(
    workout: Workout,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit = {},
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ConfirmationDialog(
            title = stringResource(Res.string.delete_workout_record),
            text = stringResource(Res.string.delete_workout_explanation),
            confirmText = stringResource(Res.string.delete_workout),
            dismissText = stringResource(Res.string.cancel),
            onDismiss = { showDialog = false },
            onConfirm = {
                showDialog = false
                onDelete()
            },
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(workout.date.toString(), fontWeight = FontWeight.Bold)

            IconButton({ showDialog = true }) {
                Icon(Icons.Default.Delete, stringResource(Res.string.delete_workout_record))
            }
        }

        Spacer(Modifier.height(32.dp))

        LazyColumn(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(workout.blocks) { block ->
                WorkoutBlockItem(workout, block, readOnly = true)
            }
        }
    }
}
