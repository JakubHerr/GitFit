package io.github.jakubherr.gitfit.domain.repository

import io.github.jakubherr.gitfit.domain.model.Plan
import kotlinx.coroutines.flow.Flow

interface PlanRepository {
    fun getPredefinedPlans(): Flow<List<Plan>>

    fun getCustomPlans(userId: String): Flow<List<Plan>>

    suspend fun saveCustomPlan(
        userId: String,
        plan: Plan,
    )

    suspend fun saveDefaultPlan(
        plan: Plan,
    )

    suspend fun deleteCustomPlan(
        userId: String,
        planId: String,
    )

    suspend fun deleteAllCustomPlans(userId: String): Result<Unit>

    suspend fun getCustomPlan(
        userId: String,
        planId: String,
    ): Plan?
}
