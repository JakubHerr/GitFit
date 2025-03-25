package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.PlanRepository
import io.github.jakubherr.gitfit.domain.model.Plan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebasePlanRepository: PlanRepository {
    private val firestore = Firebase.firestore
    private val planRef = firestore.collection("PLANS")
    private fun userPlanRef(userId: String) = firestore.collection("USERS").document(userId).collection("PLANS")
    private val context = Dispatchers.IO

    override suspend fun getPredefinedPlans() {
        TODO("Not yet implemented")
    }

    override suspend fun saveCustomPlan(userId: String, plan: Plan) {
        withContext(context) {
            val id = userPlanRef(userId).document.id
            userPlanRef(userId).document(id).set(plan.copy(id = id))
        }
    }

    override suspend fun deleteCustomPlan(userId: String, planId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCustomPlans(userId: String) {
        TODO("Not yet implemented")
    }
}