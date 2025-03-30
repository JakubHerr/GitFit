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
data class ExerciseDetailRoute(val exerciseId: String)

@Serializable
object CreateExerciseRoute

@Serializable
object MeasurementRoute

@Serializable
object PlanOverviewRoute

@Serializable
class PlanDetailRoute(val planId: String)

@Serializable
object PlanCreationRoute

@Serializable
class PlanningWorkoutRoute(val workoutIdx: Int)

@Serializable
object TrendsRoute

@Serializable
object SettingsRoute
