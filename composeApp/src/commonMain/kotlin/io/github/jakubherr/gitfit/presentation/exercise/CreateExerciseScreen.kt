package io.github.jakubherr.gitfit.presentation.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateMap
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
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Use case: Add custom exercise
@Composable
fun CreateExerciseScreenRoot(
    vm: ExerciseViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onExerciseCreated: () -> Unit = {},
) {
    CreateExerciseScreen(Modifier.fillMaxSize()) { action ->
        vm.onAction(action)
        if (action is ExerciseAction.ExerciseCreated) onExerciseCreated() // TODO this might need to wait for vm?
    }
}

@Composable
fun CreateExerciseScreen(
    modifier: Modifier = Modifier,
    onAction: (ExerciseAction) -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val primaryMuscle = remember { MuscleGroup.entries.map { it to false }.toMutableStateMap() }
    val secondaryMuscle = remember { MuscleGroup.entries.map { it to false }.toMutableStateMap() }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // name
        TextField(
            name,
            { name = it },
            label = { Text(stringResource(Res.string.name)) },
        )
        // description

        // primary muscles
        Text(stringResource(Res.string.primary_muscle))
        SelectMuscleGroups(primaryMuscle) { primaryMuscle[it] = !primaryMuscle[it]!! }

        // secondary muscles
        Text(stringResource(Res.string.secondary_muscle))
        SelectMuscleGroups(secondaryMuscle) { secondaryMuscle[it] = !secondaryMuscle[it]!! }

        Row {
            Button({
                onAction(
                    ExerciseAction.ExerciseCreated(
                        Exercise(
                            "",
                            name,
                            description,
                            primaryMuscle.selected,
                            secondaryMuscle.selected,
                        ),
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

@Composable
fun SelectMuscleGroups(
    muscleGroups: Map<MuscleGroup, Boolean>,
    modifier: Modifier = Modifier,
    onMuscleGroupToggle: (MuscleGroup) -> Unit = {},
) {
    val bruuh = muscleGroups.toList()

    LazyRow(Modifier.fillMaxWidth().wrapContentHeight()) {
        items(bruuh) { pair ->
            FilterChip(
                selected = pair.second,
                onClick = { onMuscleGroupToggle(pair.first) },
                label = { Text(pair.first.name) },
            )
        }
    }
}

private val Map<MuscleGroup, Boolean>.selected get() = entries.mapNotNull { if (it.value) it.key else null }
