package io.github.jakubherr.gitfit.presentation.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.cancel
import gitfit.composeapp.generated.resources.cancel_exercise_creation
import gitfit.composeapp.generated.resources.cancel_exercise_creation_explanation
import gitfit.composeapp.generated.resources.delete_exercise
import gitfit.composeapp.generated.resources.enum_muscle_group_abs
import gitfit.composeapp.generated.resources.enum_muscle_group_arms
import gitfit.composeapp.generated.resources.enum_muscle_group_back
import gitfit.composeapp.generated.resources.enum_muscle_group_chest
import gitfit.composeapp.generated.resources.enum_muscle_group_forearms
import gitfit.composeapp.generated.resources.enum_muscle_group_legs
import gitfit.composeapp.generated.resources.enum_muscle_group_shoulders
import gitfit.composeapp.generated.resources.name
import gitfit.composeapp.generated.resources.primary_muscle
import gitfit.composeapp.generated.resources.save_exercise
import gitfit.composeapp.generated.resources.secondary_muscle
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.MuscleGroup
import io.github.jakubherr.gitfit.presentation.shared.ConfirmationDialog
import io.github.jakubherr.gitfit.presentation.shared.MultipleChoiceChipSelection
import io.github.jakubherr.gitfit.presentation.shared.OnBackPress
import io.github.jakubherr.gitfit.presentation.shared.SingleChoiceChipSelection
import io.github.jakubherr.gitfit.presentation.shared.StringInputField
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExerciseCreateScreenRoot(
    modifier: Modifier = Modifier,
    onExerciseCreated: (Exercise) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    ExerciseCreateScreen(
        Modifier.fillMaxSize(),
        onExerciseCreated = { onExerciseCreated(it) },
        onCancel = onCancel
    )
}

@Composable
fun ExerciseCreateScreen(
    modifier: Modifier = Modifier,
    onExerciseCreated: (Exercise) -> Unit = {},
    onCancel: () -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var selectedPrimaryMuscle by remember { mutableStateOf(MuscleGroup.entries.first()) }
    val selectedSecondaryMuscle = remember { mutableStateListOf<MuscleGroup>() }

    var showDialog by remember { mutableStateOf(false) }
    OnBackPress { showDialog = true }
    if (showDialog) {
        ConfirmationDialog(
            title = stringResource(Res.string.cancel_exercise_creation),
            text = stringResource(Res.string.cancel_exercise_creation_explanation),
            confirmText = stringResource(Res.string.delete_exercise),
            dismissText = stringResource(Res.string.cancel),
            onDismiss = { showDialog = false },
            onConfirm = {
                showDialog = false
                onCancel()
            }
        )
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StringInputField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(Res.string.name)) },
            maxLength = 20,
        )

        Text(stringResource(Res.string.primary_muscle))
        val translations = listOf(
            stringResource(Res.string.enum_muscle_group_arms),
            stringResource(Res.string.enum_muscle_group_legs),
            stringResource(Res.string.enum_muscle_group_shoulders),
            stringResource(Res.string.enum_muscle_group_back),
            stringResource(Res.string.enum_muscle_group_abs),
            stringResource(Res.string.enum_muscle_group_chest),
            stringResource(Res.string.enum_muscle_group_forearms),
        )
        SingleChoiceChipSelection(
            MuscleGroup.entries,
            translations,
            selectedPrimaryMuscle
        ) { selectedPrimaryMuscle = it }

        Text(stringResource(Res.string.secondary_muscle))
        MultipleChoiceChipSelection(
            MuscleGroup.entries,
            translations,
            selectedSecondaryMuscle
        ) {
            if (selectedSecondaryMuscle.contains(it)) selectedSecondaryMuscle.remove(it) else selectedSecondaryMuscle.add(it)
        }

        Row {
            Button({
                if (name.isBlank()) return@Button

                onExerciseCreated(
                    Exercise(
                        "",
                        name,
                        "",
                        isCustom = true,
                        selectedPrimaryMuscle,
                        selectedSecondaryMuscle,
                    ),
                )
            }) {
                Text(stringResource(Res.string.save_exercise))
            }
            Button({ showDialog = true }) {
                Text(stringResource(Res.string.cancel))
            }
        }
    }
}
