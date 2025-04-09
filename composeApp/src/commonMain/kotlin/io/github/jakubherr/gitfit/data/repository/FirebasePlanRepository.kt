package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.repository.AuthError
import io.github.jakubherr.gitfit.domain.repository.PlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FirebasePlanRepository : PlanRepository {
    private val firestore = Firebase.firestore
    private val context = Dispatchers.IO
    private val planRef = firestore.collection("PLANS")

    private fun userPlanRef(userId: String) = firestore.collection("USERS").document(userId).collection("PLANS")

    override fun getPredefinedPlans(): Flow<List<Plan>> {
        return planRef.snapshots.map { snapshot ->
            snapshot.documents.map { it.data<Plan>() }
        }
    }

    override fun getCustomPlans(userId: String): Flow<List<Plan>> {
        return userPlanRef(userId).snapshots.map { snapshot ->
            snapshot.documents.map { it.data<Plan>() }
        }
    }

    // if a plan is already associated with an id, it will get overwritten
    override suspend fun saveCustomPlan(
        userId: String,
        plan: Plan,
    ) {
        withContext(context) {
            val id = plan.id.ifBlank { userPlanRef(userId).document.id }
            userPlanRef(userId).document(id).set(plan.copy(id = id))
        }
    }

    override suspend fun deleteCustomPlan(
        userId: String,
        planId: String,
    ) {
        withContext(context) {
            userPlanRef(userId).document(planId).delete()
        }
    }

    override suspend fun deleteAllCustomPlans(userId: String): Result<Unit> {
        userId.ifBlank { return Result.failure(AuthError.UserLoggedOut) }

        return withContext(context) {
            userPlanRef(userId).get().documents.forEach { document ->
                try {
                    userPlanRef(userId).document(document.id).delete()
                } catch (e: Exception) {
                    return@withContext Result.failure(e)
                }
            }
            Result.success(Unit)
        }
    }

    override suspend fun getCustomPlan(
        userId: String,
        planId: String,
    ): Plan? {
        if (userId.isBlank()) return null

        return withContext(context) {
            val plan = userPlanRef(userId).document(planId).get()
            if (plan.exists) plan.data<Plan>() else null
        }
    }
}
