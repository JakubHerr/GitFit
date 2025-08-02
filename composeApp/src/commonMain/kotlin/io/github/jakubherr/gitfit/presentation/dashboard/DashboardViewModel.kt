package io.github.jakubherr.gitfit.presentation.dashboard

import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel()

sealed interface DashboardAction {
    class PlannedWorkoutClick(
        val workoutId: String,
    ) : DashboardAction

    object UnplannedWorkoutClick : DashboardAction

    object ResumeWorkoutClick : DashboardAction
}
