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
class AddExerciseToWorkoutRoute(val workoutId: String)

@Serializable
data class ExerciseDetailRoute(val id: String)

@Serializable
object CreateExerciseRoute

@Serializable
object MeasurementRoute

@Serializable
object PlanningRoute

@Serializable
object TrendsRoute

@Serializable
object SettingsRoute

