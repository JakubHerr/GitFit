package io.github.jakubherr.gitfit.presentation.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import gitfit.composeapp.generated.resources.name
import gitfit.composeapp.generated.resources.primary_muscle
import gitfit.composeapp.generated.resources.save_exercise
import gitfit.composeapp.generated.resources.secondary_muscle
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.MuscleGroup
import io.github.jakubherr.gitfit.presentation.shared.MultipleChoiceChipSelection
import io.github.jakubherr.gitfit.presentation.shared.SingleChoiceChipSelection
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateExerciseScreenRoot(
    modifier: Modifier = Modifier,
    onExerciseCreated: (Exercise) -> Unit = {},
) {
    CreateExerciseScreen(Modifier.fillMaxSize()) { exercise ->
        onExerciseCreated(exercise)
    }
}

@Composable
fun CreateExerciseScreen(
    modifier: Modifier = Modifier,
    onExerciseCreated: (Exercise) -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPrimaryMuscle by remember { mutableStateOf(MuscleGroup.entries.first()) }
    val selectedSecondaryMuscle = remember { mutableStateListOf<MuscleGroup>() }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            name,
            { name = it },
            label = { Text(stringResource(Res.string.name)) },
        )

        Text(stringResource(Res.string.primary_muscle))
        SingleChoiceChipSelection(
            MuscleGroup.entries,
            selectedPrimaryMuscle
        ) { selectedPrimaryMuscle = it }

        Text(stringResource(Res.string.secondary_muscle))
        MultipleChoiceChipSelection(
            MuscleGroup.entries,
            selectedSecondaryMuscle
        ) {
            if (selectedSecondaryMuscle.contains(it)) selectedSecondaryMuscle.remove(it) else selectedSecondaryMuscle.add(it)
        }

        Row {
            Button({
                onExerciseCreated(
                    Exercise(
                        "",
                        name,
                        description,
                        selectedPrimaryMuscle,
                        selectedSecondaryMuscle,
                    ),
                )
            }) {
                Text(stringResource(Res.string.save_exercise))
            }
            Button({}) {
                Text(stringResource(Res.string.cancel))
            }
        }
    }
}
