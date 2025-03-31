package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.repository.PlanRepository
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FirebasePlanRepository: PlanRepository {
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
    override suspend fun saveCustomPlan(userId: String, plan: Plan) {
        withContext(context) {
            val id = plan.id.ifBlank { userPlanRef(userId).document.id }
            userPlanRef(userId).document(id).set(plan.copy(id = id))
        }
    }

    override suspend fun getCustomWorkout(userId: String, planId: String, workoutIdx: Int): WorkoutPlan {
        return withContext(context) {
            val plan = userPlanRef(userId).document(planId).get().data<Plan>()
            val workout = plan.workoutPlans.find { it.idx == workoutIdx }
            return@withContext workout!! // TODO error checking
        }
    }

    override suspend fun deleteCustomPlan(userId: String, planId: String) {
        withContext(context) {
            userPlanRef(userId).document(planId).delete()
        }
    }

    override suspend fun deleteCustomPlans(userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomPlan(userId: String, planId: String): Plan? {
        if (userId.isBlank()) return null

        return withContext(context) {
            val plan = userPlanRef(userId).document(planId).get()
            if (plan.exists) plan.data<Plan>() else null
        }
    }
}