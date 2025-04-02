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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.presentation.graph.BasicLineGraph
import io.github.jakubherr.gitfit.presentation.graph.ExerciseMetric
import io.github.jakubherr.gitfit.presentation.graph.GraphAction
import io.github.jakubherr.gitfit.presentation.graph.GraphViewModel
import io.github.jakubherr.gitfit.presentation.shared.SingleChoiceChipSelection
import io.github.koalaplot.core.xygraph.DefaultPoint
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExerciseDetailScreenRoot(
    modifier: Modifier = Modifier,
    graphViewModel: GraphViewModel = koinViewModel(),
    exerciseViewModel: ExerciseViewModel = koinViewModel(),
    onBack: () -> Unit = {},
) {
    val data by graphViewModel.dataPoints.collectAsStateWithLifecycle()
    val exerciseFetch = exerciseViewModel.fetchedExercise

    when (exerciseFetch) {
        is ExerciseFetchResult.Loading -> CircularProgressIndicator()
        is ExerciseFetchResult.Failure -> Text("Some error occurred")
        is ExerciseFetchResult.Success -> {
            ExerciseDetailScreen(
                exercise = exerciseFetch.exercise,
                graphData = data,
                selectedMetric = graphViewModel.selectedMetric,
                onGraphAction = { graphViewModel.onAction(it) },
                onExerciseAction = {
                    exerciseViewModel.onAction(it)
                    onBack()
                }
            )
        }
    }
}

// use case: show a detail of exercise and records, graphs
@Composable
fun ExerciseDetailScreen(
    exercise: Exercise,
    graphData: List<DefaultPoint<String, Int>>,
    selectedMetric: ExerciseMetric,
    modifier: Modifier = Modifier,
    onGraphAction: (GraphAction) -> Unit = {},
    onExerciseAction: (ExerciseAction) -> Unit = {}
) {
    LaunchedEffect(exercise) {
        onGraphAction(GraphAction.ExerciseMetricSelected(exercise.id, ExerciseMetric.HEAVIEST_WEIGHT))
    }

    // name, description, primary, secondary muscle etc.
    Column(modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(exercise.name)

            // TODO: are you sure? dialog
            if (exercise.isCustom) {
                IconButton({ onExerciseAction(ExerciseAction.DeleteCustomExercise(exercise.id)) }) {
                    Icon(Icons.Default.Delete, "")
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
                val unit = if (selectedMetric == ExerciseMetric.TOTAL_REPETITIONS) "reps" else "kg"
                Text("${maxValue.x} - ${maxValue.y} $unit")
            }
        }

        BasicLineGraph(
            graphData,
            Modifier.fillMaxWidth().height(256.dp),
            "Last 10 workouts - ${exercise.name}",
        )

        SingleChoiceChipSelection(
            ExerciseMetric.entries,
            selected = selectedMetric,
            modifier = Modifier.padding(16.dp),
            onChoiceSelected = { metric ->
                println("DBG: ${metric.name} selected")
                onGraphAction(GraphAction.ExerciseMetricSelected(exercise.id, metric))
            }
        )
    }
}
