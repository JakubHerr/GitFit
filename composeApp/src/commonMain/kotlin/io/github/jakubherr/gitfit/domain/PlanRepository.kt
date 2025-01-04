package io.github.jakubherr.gitfit.domain

import io.github.jakubherr.gitfit.domain.model.Plan

interface PlanRepository {
    suspend fun getPredefinedPlans()

    suspend fun createCustomPlan(plan: Plan)

    // modify existing plan?
    suspend fun deleteCustomPlan(planId: String)

    suspend fun deleteAllCustomPlans(userId: String)
}
