package io.github.jakubherr.gitfit.domain

import io.github.jakubherr.gitfit.domain.model.Plan

interface PlanRepository {
    suspend fun getPredefinedPlans()

    suspend fun saveCustomPlan(userId: String, plan: Plan)

    // modify existing plan?
    suspend fun deleteCustomPlan(userId: String, planId: String)

    suspend fun deleteCustomPlans(userId: String)
}
