package io.github.jakubherr.gitfit.presentation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object RegisterRoute

@Serializable
object ResetPasswordRoute

@Serializable
object VerifyEmailRoute

@Serializable
object OnboardingRoute

@Serializable
object DashboardRoute

@Serializable
object WorkoutInProgressRoute

@Serializable
object ExerciseListRoute

@Serializable
class AddExerciseToWorkoutRoute(val workoutId: String)

@Serializable
class AddExerciseToPlanRoute(val workoutIdx: Int)

@Serializable
data class ExerciseDetailRoute(val exerciseId: String, val isCustom: Boolean)

@Serializable
object CreateExerciseRoute

@Serializable
object MeasurementRoute

@Serializable
object MeasurementAddEditRoute

@Serializable
object PlanOverviewRoute

@Serializable
class PlanDetailRoute(val planId: String)

@Serializable
object PlanCreationRoute

@Serializable
class PlanningWorkoutRoute(val workoutIdx: Int)

@Serializable
class EditProgressionRoute(val workoutIdx: Int, val blockIdx: Int)

@Serializable
object HistoryRoute

@Serializable
object WorkoutHistoryRoute

@Serializable
object WorkoutDetailRoute

@Serializable
object SettingsRoute
