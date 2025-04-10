package io.github.jakubherr.gitfit.presentation.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.cancel
import gitfit.composeapp.generated.resources.delete
import gitfit.composeapp.generated.resources.delete_custom_exercise
import gitfit.composeapp.generated.resources.delete_custom_exercise_explanation
import gitfit.composeapp.generated.resources.enum_exercise_metric_best_set_volume
import gitfit.composeapp.generated.resources.enum_exercise_metric_heaviest_weight
import gitfit.composeapp.generated.resources.enum_exercise_metric_total_repetitions
import gitfit.composeapp.generated.resources.enum_exercise_metric_total_workout_volume
import gitfit.composeapp.generated.resources.error_exercise_not_found
import gitfit.composeapp.generated.resources.kg
import gitfit.composeapp.generated.resources.last_10_workouts
import gitfit.composeapp.generated.resources.reps
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.presentation.graph.BasicLineGraph
import io.github.jakubherr.gitfit.presentation.graph.ExerciseMetric
import io.github.jakubherr.gitfit.presentation.graph.GraphAction
import io.github.jakubherr.gitfit.presentation.graph.GraphViewModel
import io.github.jakubherr.gitfit.presentation.shared.ConfirmationDialog
import io.github.jakubherr.gitfit.presentation.shared.SingleChoiceChipSelection
import io.github.koalaplot.core.xygraph.DefaultPoint
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExerciseDetailScreenRoot(
    modifier: Modifier = Modifier,
    graphViewModel: GraphViewModel,
    exerciseViewModel: ExerciseViewModel,
    onBack: () -> Unit = {},
) {
    val data by graphViewModel.data.collectAsStateWithLifecycle(emptyList())
    val selectedMetric by graphViewModel.selectedMetric.collectAsStateWithLifecycle()
    val exercise = exerciseViewModel.selectedExercise
    var showDialog by remember { mutableStateOf(false) }

    exercise?.let {
        if (showDialog) {
            ConfirmationDialog(
                title = stringResource(Res.string.delete_custom_exercise),
                text = stringResource(Res.string.delete_custom_exercise_explanation),
                confirmText = stringResource(Res.string.delete),
                dismissText = stringResource(Res.string.cancel),
                onDismiss = { showDialog = false },
                onConfirm = {
                    showDialog = false
                    exerciseViewModel.onAction(ExerciseAction.DeleteCustomExercise(exercise.id))
                    onBack()
                },
            )
        }

        ExerciseDetailScreen(
            exercise = exercise,
            graphData = data,
            selectedMetric = selectedMetric,
            onGraphAction = { graphViewModel.onAction(it) },
            onDeleteExercise = { showDialog = true },
        )
    }

    if (exercise == null) {
        Text(stringResource(Res.string.error_exercise_not_found))
    }
}

@Composable
fun ExerciseDetailScreen(
    exercise: Exercise,
    graphData: List<DefaultPoint<String, Int>>,
    selectedMetric: ExerciseMetric,
    modifier: Modifier = Modifier,
    onGraphAction: (GraphAction) -> Unit = {},
    onDeleteExercise: (String) -> Unit = {},
) {
    LaunchedEffect(exercise) {
        onGraphAction(GraphAction.ExerciseAndMetricSelected(exercise, ExerciseMetric.HEAVIEST_WEIGHT))
    }

    Column(modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(exercise.name)

            if (exercise.isCustom) {
                IconButton({ onDeleteExercise(exercise.id) }) {
                    Icon(Icons.Default.Delete, stringResource(Res.string.delete_custom_exercise))
                }
            }
        }

        Text(exercise.primaryMuscle.name, fontWeight = FontWeight.Bold)
        if (exercise.secondaryMuscle.isNotEmpty()) Text(exercise.secondaryMuscle.joinToString())

        Spacer(Modifier.height(16.dp))

        // TODO: time scale selection
        Row {
            val maxValue = graphData.maxByOrNull { it.y }
            maxValue?.let {
                val unit =
                    if (selectedMetric == ExerciseMetric.TOTAL_REPETITIONS) {
                        stringResource(Res.string.reps)
                    } else {
                        stringResource(Res.string.kg)
                    }
                Text("${maxValue.x} - ${maxValue.y} $unit")
            }
        }

        BasicLineGraph(
            graphData,
            Modifier.fillMaxWidth().height(256.dp),
            "${stringResource(Res.string.last_10_workouts)} - ${exercise.name}",
        )

        val translations =
            listOf(
                stringResource(Res.string.enum_exercise_metric_heaviest_weight),
                stringResource(Res.string.enum_exercise_metric_best_set_volume),
                stringResource(Res.string.enum_exercise_metric_total_workout_volume),
                stringResource(Res.string.enum_exercise_metric_total_repetitions),
            )

        SingleChoiceChipSelection(
            ExerciseMetric.entries,
            labels = translations,
            selected = selectedMetric,
            modifier = Modifier.padding(16.dp),
            onChoiceSelected = { metric ->
                onGraphAction(GraphAction.ExerciseAndMetricSelected(exercise, metric))
            },
        )
    }
}
