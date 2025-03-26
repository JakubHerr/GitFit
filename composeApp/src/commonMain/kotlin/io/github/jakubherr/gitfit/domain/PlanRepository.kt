package io.github.jakubherr.gitfit.domain

import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface PlanRepository {
    fun getPredefinedPlans(): Flow<List<Plan>>

    fun getCustomPlans(userId: String): Flow<List<Plan>>

    suspend fun saveCustomPlan(userId: String, plan: Plan)

    suspend fun saveCustomWorkout(userId: String, workoutPlan: WorkoutPlan)

    fun getCustomWorkouts(userId: String): Flow<List<WorkoutPlan>>

    // modify existing plan?
    suspend fun deleteCustomPlan(userId: String, planId: String)

    suspend fun deleteCustomPlans(userId: String)
}
