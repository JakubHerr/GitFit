package io.github.jakubherr.gitfit.presentation

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
object LoginRoute

@Serializable
@Keep
object RegisterRoute

@Serializable
@Keep
object ResetPasswordRoute

@Serializable
@Keep
object VerifyEmailRoute

@Serializable
@Keep
object OnboardingRoute

@Serializable
@Keep
object DashboardRoute

@Serializable
@Keep
object WorkoutInProgressRoute

@Serializable
@Keep
object ExerciseListRoute

@Serializable
@Keep
class AddExerciseToWorkoutRoute(val workoutId: String)

@Serializable
@Keep
class AddExerciseToPlanRoute(val workoutIdx: Int)

@Serializable
@Keep
data class ExerciseDetailRoute(val exerciseId: String, val isCustom: Boolean)

@Serializable
@Keep
object CreateExerciseRoute

@Serializable
@Keep
object MeasurementRoute

@Serializable
@Keep
object MeasurementAddEditRoute

@Serializable
@Keep
object PlanOverviewRoute

@Serializable
@Keep
class PlanDetailRoute(val planId: String)

@Serializable
@Keep
object PlanCreationRoute

@Serializable
@Keep
class PlanningWorkoutRoute(val workoutIdx: Int)

@Serializable
@Keep
class EditProgressionRoute(val workoutIdx: Int, val blockIdx: Int)

@Serializable
@Keep
object HistoryRoute

@Serializable
@Keep
object WorkoutHistoryRoute

@Serializable
@Keep
object WorkoutDetailRoute

@Serializable
@Keep
object SettingsRoute
