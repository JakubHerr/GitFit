package io.github.jakubherr.gitfit.presentation.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.mockExercise
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
) {
    val data by graphViewModel.dataPoints.collectAsStateWithLifecycle()
    val exercise = exerciseViewModel.lastFetchedExercise

    if (exercise != null) {
        ExerciseDetailScreen(
            exercise = exercise,
            graphData = data,
            selectedMetric = graphViewModel.selectedMetric,
            onAction = { graphViewModel.onAction(it) }
        )
    } else {
        // TODO: loading/exercise not found UI
    }
}

// use case: show a detail of exercise and records, graphs
@Composable
fun ExerciseDetailScreen(
    exercise: Exercise = mockExercise,
    graphData: List<DefaultPoint<String, Int>>,
    selectedMetric: ExerciseMetric,
    modifier: Modifier = Modifier,
    onAction: (GraphAction) -> Unit = {},
) {
    LaunchedEffect(exercise) {
        onAction(GraphAction.ExerciseMetricSelected(exercise.id, ExerciseMetric.HEAVIEST_WEIGHT))
    }

    // name, description, primary, secondary muscle etc.
    Column(modifier.fillMaxSize()) {
        Text(exercise.name)
        Text(exercise.primaryMuscle.joinToString(), fontWeight = FontWeight.Bold)
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
                onAction(GraphAction.ExerciseMetricSelected(exercise.id, metric))
            }
        )
    }
}
