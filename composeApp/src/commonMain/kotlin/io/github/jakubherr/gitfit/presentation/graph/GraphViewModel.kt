package io.github.jakubherr.gitfit.presentation.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.MeasurementRepository
import io.github.jakubherr.gitfit.domain.WorkoutRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.MuscleGroup
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.koalaplot.core.xygraph.DefaultPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class GraphViewModel(
    private val workoutRepository: WorkoutRepository,
    private val measurementRepository: MeasurementRepository,
) : ViewModel() {
    var selectedMetric by mutableStateOf(ExerciseMetric.HEAVIEST_WEIGHT)
        private set

    private val lastTen = workoutRepository.getCompletedWorkouts().map { wo -> wo.sortedBy { it.date } }.take(10).stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(5_000L),
    )

    private val _dataPoints: MutableStateFlow<List<DefaultPoint<String, Int>>> = MutableStateFlow(emptyList())
    val dataPoints: StateFlow<List<DefaultPoint<String, Int>>> = _dataPoints

    private val debugExercise = Exercise(
        id = "9GwK3U2bLiQFdsFXo1nI",
        name = "Overhead Press",
        description = null,
        primaryMuscle = listOf(MuscleGroup.SHOULDERS),
        secondaryMuscle = listOf(MuscleGroup.ARMS),
    )

    init {
        viewModelScope.launch {
            lastTen.collect { workouts ->
                println("DBG: collected ${workouts.size} workouts")
                changeExerciseMetric(selectedMetric)
            }
        }
    }

    // measurement data

    fun onAction(action: GraphAction) {
        when (action) {
            is GraphAction.ExerciseMetricSelected -> changeExerciseMetric(action.metric)
        }
    }

    private fun changeExerciseMetric(metric: ExerciseMetric) {
        selectedMetric = metric

        viewModelScope.launch {
            val list = when (metric) {
                ExerciseMetric.HEAVIEST_WEIGHT -> lastTen.value.toDataPoints { it.getExerciseHeaviestWeight(debugExercise.id)?.toInt() }
                ExerciseMetric.BEST_SET_VOLUME -> lastTen.value.toDataPoints { it.getExerciseBestSetVolume(debugExercise.id)?.toInt() }
                ExerciseMetric.TOTAL_WORKOUT_VOLUME -> lastTen.value.toDataPoints { it.getExerciseTotalWorkoutVolume(debugExercise.id)?.toInt() }
                ExerciseMetric.TOTAL_REPETITIONS -> lastTen.value.toDataPoints { it.getExerciseTotalRepetitions(debugExercise.id)?.toInt() }
            }

            _dataPoints.emit(list)
        }
    }

    private fun <T> List<Workout>.toDataPoints(calculation: (Workout) -> T?): List<DefaultPoint<String, T>> {
        val list = buildList {
            this@toDataPoints.forEach { workout ->
                val metric = calculation(workout)
                if (metric != null) add(
                    DefaultPoint(
                        "${workout.date.dayOfMonth}.${workout.date.monthNumber}.",
                        metric as T
                    )
                )
            }
        }

        return list
    }
}

sealed interface GraphAction {
    class ExerciseMetricSelected(val metric: ExerciseMetric) : GraphAction
}