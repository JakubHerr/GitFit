package io.github.jakubherr.gitfit.presentation.graph

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.MeasurementRepository
import io.github.jakubherr.gitfit.domain.WorkoutRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.MuscleGroup
import io.github.jakubherr.gitfit.domain.model.Workout
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
                    getExerciseHeaviestWeight(workout, debugExercise)
                }
            }
        }
    }

    private fun getExerciseHeaviestWeight(workout: Workout, exercise: Exercise) {
        val exerciseSeries = workout.blocks.filter { it.exercise.id == exercise.id }.map { it.series }.flatten()
        val heaviestSet = exerciseSeries.maxByOrNull { it.weight ?: 0.0 }
        println("DBG: Workout ${workout.id}, exercise ${exercise.name}, heaviest weight: ${heaviestSet?.weight}")
    }

    private fun getExerciseBestSetVolume(workout: Workout, exercise: Exercise) {
        TODO()
    }

    private fun getExerciseTotalWorkoutVolume(workout: Workout, exercise: Exercise) {
        TODO()
    }

    private fun getExerciseTotalRepetitions(workout: Workout, exercise: Exercise) {
        TODO()
    }

    // measurement data

    fun onAction() {

    }
}

sealed interface GraphAction {
    class ExerciseMetricSelected(val metric: ExerciseMetric) : GraphAction
}