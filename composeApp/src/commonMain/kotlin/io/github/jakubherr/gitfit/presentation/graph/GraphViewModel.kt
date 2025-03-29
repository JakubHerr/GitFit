package io.github.jakubherr.gitfit.presentation.graph

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.MeasurementRepository
import io.github.jakubherr.gitfit.domain.WorkoutRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.MuscleGroup
import kotlinx.coroutines.launch

class GraphViewModel(
    private val workoutRepository: WorkoutRepository,
    private val measurementRepository: MeasurementRepository,
): ViewModel() {
    // exercise data
    val workouts = workoutRepository.getCompletedWorkouts()

    val debugExercise = Exercise(
        id = "9GwK3U2bLiQFdsFXo1nI",
        name = "Overhead Press",
        description = null,
        primaryMuscle = listOf(MuscleGroup.SHOULDERS),
        secondaryMuscle = listOf(MuscleGroup.ARMS),
    )

    init {
        viewModelScope.launch {
            workouts.collect { workouts ->
                println("DBG: collected ${workouts.size} workouts")

                workouts.forEach { workout ->
                    workout.getExerciseHeaviestWeight(debugExercise.id)
                    workout.getExerciseBestSetVolume(debugExercise.id)
                    workout.getExerciseTotalWorkoutVolume(debugExercise.id)
                    workout.getExerciseTotalRepetitions(debugExercise.id)
                }
            }
        }
    }

    // measurement data

    fun onAction() {

    }
}

sealed interface GraphAction {
    class ExerciseMetricSelected(val metric: ExerciseMetric) : GraphAction
}