package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.AuthRepository
import io.github.jakubherr.gitfit.domain.PlanRepository
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.ProgressionSettings
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class PlanningViewModel(
    private val planRepository: PlanRepository,
    private val authRepository: AuthRepository,
): ViewModel() {
    var plan: Plan by mutableStateOf(Plan.Empty)
    var error: Plan.Error? by mutableStateOf(null)

    val userPlans get() =
        if (authRepository.currentUser.loggedIn) planRepository.getCustomPlans(authRepository.currentUser.id)
        else emptyFlow()

    val predefinedPlans = planRepository.getPredefinedPlans()

    fun onAction(action: PlanAction) {
        when (action) {
            is PlanAction.SavePlan -> savePlan()
            is PlanAction.RenamePlan -> plan = plan.copy(name = action.name)
            is PlanAction.EditPlan -> plan = action.plan
            is PlanAction.DiscardPlan -> plan = Plan.Empty
            is PlanAction.DeletePlan -> deletePlan(action.planId)

            is PlanAction.AddWorkout -> plan = plan.addWorkoutPlan(action.workout)
            is PlanAction.SaveWorkout -> validateWorkoutPlan(action.workout)
            is PlanAction.DeleteWorkout -> plan = plan.removeWorkoutPlan(action.workout)

            is PlanAction.AddExercise -> plan = plan.addExercise(action.workoutIdx, action.exercise)
            is PlanAction.RemoveExercise -> plan = plan.removeBlock(action.workout, action.block)

            is PlanAction.AddSet -> plan = plan.addSeries(action.workout, action.block)
            is PlanAction.EditSet -> plan = plan.updateSeries(action.workout, action.block, action.set)
            is PlanAction.RemoveSet -> plan = plan.removeSeries(action.workout, action.block, action.set)

            is PlanAction.DeleteProgression -> plan = plan.setProgression(action.workout, action.block, null)
            is PlanAction.SaveProgression -> plan = plan.setProgression(action.workout, action.block, action.progression)

            PlanAction.ErrorHandled -> error = null
        }
    }

    private fun savePlan() {
        val user = authRepository.currentUser
        if (!user.loggedIn) return

        error = plan.error
        if (error != null) return

        viewModelScope.launch {
            planRepository.saveCustomPlan(user.id, plan)
            plan = Plan.Empty
        }
    }

    private fun deletePlan(planId: String) {
        val user = authRepository.currentUser
        if (!user.loggedIn) return

        viewModelScope.launch { planRepository.deleteCustomPlan(user.id, planId) }
    }

    private fun validateWorkoutPlan(workoutPlan: WorkoutPlan) {
        val workoutError = workoutPlan.toWorkout().error ?: return
        error = Plan.Error.InvalidWorkout(workoutError)
    }
}

sealed interface PlanAction {
    object SavePlan : PlanAction
    class RenamePlan(val name: String) : PlanAction
    class EditPlan(val plan: Plan) : PlanAction
    object DiscardPlan : PlanAction
    class DeletePlan(val planId: String) : PlanAction

    class AddWorkout(val workout: WorkoutPlan) : PlanAction
    class SaveWorkout(val workout: WorkoutPlan) : PlanAction
    class DeleteWorkout(val workout: WorkoutPlan) : PlanAction

    class AddExercise(val workoutIdx: Int, val exercise: Exercise) : PlanAction
    class RemoveExercise(val workout: WorkoutPlan, val block: Block) : PlanAction

    class AddSet(val workout: WorkoutPlan, val block: Block) : PlanAction
    class EditSet(val workout: WorkoutPlan, val block: Block, val set: Series) : PlanAction
    class RemoveSet(val workout: WorkoutPlan, val block: Block, val set: Series) : PlanAction

    class SaveProgression(val workout: WorkoutPlan, val block: Block, val progression: ProgressionSettings) : PlanAction
    class DeleteProgression(val workout: WorkoutPlan, val block: Block) : PlanAction

    object ErrorHandled : PlanAction
}
