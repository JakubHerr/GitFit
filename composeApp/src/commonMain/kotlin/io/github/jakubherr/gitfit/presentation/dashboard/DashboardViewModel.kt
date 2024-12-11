package io.github.jakubherr.gitfit.presentation.dashboard

import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

}

sealed interface DashboardAction {
    class PlannedWorkoutClick(val id: Long) : DashboardAction
    object UnplannedWorkoutClick : DashboardAction
}