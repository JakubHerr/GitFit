package io.github.jakubherr.gitfit.presentation.graph

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.repository.WorkoutRepository
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.domain.repository.AuthRepository
import io.github.koalaplot.core.xygraph.DefaultPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take

class GraphViewModel(
    private val workoutRepository: WorkoutRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    var selectedMetric = MutableStateFlow(ExerciseMetric.HEAVIEST_WEIGHT)
        private set

    private var selectedExercise = MutableStateFlow<Exercise?>(null)

    private val currentUser = authRepository.currentUserFlow

    // TODO make state flow that will change the number of selected records
    private val lastTen = currentUser.flatMapLatest { user ->
        workoutRepository.getCompletedWorkouts(user?.id ?: "")
    }
    .map { wo ->
        wo.filter { it.hasExercise(selectedExercise.value?.id) }.sortedBy { it.date }
    }
    .take(10)
    .stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(5_000L),
    )

    val data: Flow<List<DefaultPoint<String, Int>>> =
        combine(lastTen, selectedMetric, selectedExercise) { workouts, metric, exercise ->
            if (exercise == null) return@combine emptyList()

            return@combine when (metric) {
                ExerciseMetric.HEAVIEST_WEIGHT -> workouts.toDataPoints { it.getExerciseHeaviestWeight(exercise.id)?.toInt() }
                ExerciseMetric.BEST_SET_VOLUME -> workouts.toDataPoints { it.getExerciseBestSetVolume(exercise.id)?.toInt() }
                ExerciseMetric.TOTAL_WORKOUT_VOLUME -> workouts.toDataPoints { it.getExerciseTotalWorkoutVolume(exercise.id)?.toInt() }
                ExerciseMetric.TOTAL_REPETITIONS -> workouts.toDataPoints { it.getExerciseTotalRepetitions(exercise.id)?.toInt() }
            }
        }

    fun onAction(action: GraphAction) {
        when (action) {
            is GraphAction.ExerciseAndMetricSelected -> {
                println("DBG: selecting metric ${action.metric} for exercise ${action.exercise.id}")
                selectedMetric.value = action.metric
                selectedExercise.value = action.exercise
            }
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
    class ExerciseAndMetricSelected(val exercise: Exercise, val metric: ExerciseMetric) : GraphAction
}