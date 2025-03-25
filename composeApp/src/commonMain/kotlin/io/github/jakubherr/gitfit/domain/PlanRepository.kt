package io.github.jakubherr.gitfit.domain

import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface PlanRepository {
    suspend fun getPredefinedPlans()

    suspend fun saveCustomPlan(userId: String, plan: Plan)

    suspend fun saveCustomWorkout(userId: String, workoutPlan: WorkoutPlan)

    fun getCustomWorkouts(userId: String): Flow<List<WorkoutPlan>>

    // modify existing plan?
    suspend fun deleteCustomPlan(userId: String, planId: String)

    suspend fun deleteCustomPlans(userId: String)
}
