package io.github.jakubherr.gitfit.presentation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object OnboardingRoute

@Serializable
object DashboardRoute

@Serializable
object WorkoutRoute

@Serializable
object ExerciseListRoute

@Serializable
class AddExerciseToWorkoutRoute(val workoutId: Long)

@Serializable
data class ExerciseDetailRoute(val id: Long)

@Serializable
object CreateExerciseRoute

@Serializable
object MeasurementRoute

@Serializable
object TrendsRoute

@Serializable
object SettingsRoute

