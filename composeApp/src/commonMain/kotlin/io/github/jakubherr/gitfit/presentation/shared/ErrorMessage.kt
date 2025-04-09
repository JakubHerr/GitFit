package io.github.jakubherr.gitfit.presentation.shared

import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.error_invalid_series_values
import gitfit.composeapp.generated.resources.error_no_exercise_in_workout
import gitfit.composeapp.generated.resources.error_no_series_for_exercise
import gitfit.composeapp.generated.resources.error_no_workouts_in_plan
import gitfit.composeapp.generated.resources.error_unnamed_plan
import gitfit.composeapp.generated.resources.error_unnamed_workout_plan
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.Workout
import org.jetbrains.compose.resources.getString

suspend fun Plan.Error.toMessage(): String = when (this) {
    is Plan.Error.InvalidPlanName -> getString(Res.string.error_unnamed_plan)
    is Plan.Error.InvalidWorkout -> this.error.toMessage()
    is Plan.Error.NoWorkoutInPlan -> getString(Res.string.error_no_workouts_in_plan)
}

suspend fun Workout.Error.toMessage(): String = when (this) {
    Workout.Error.BlankName -> getString(Res.string.error_unnamed_workout_plan)
    Workout.Error.InvalidSetInExercise -> getString(Res.string.error_invalid_series_values)
    Workout.Error.NoExerciseInWorkout -> getString(Res.string.error_no_exercise_in_workout)
    Workout.Error.NoSetInExercise -> getString(Res.string.error_no_series_for_exercise)
}
