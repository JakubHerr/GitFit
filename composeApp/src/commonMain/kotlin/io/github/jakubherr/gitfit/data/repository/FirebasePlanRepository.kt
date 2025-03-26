package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.PlanRepository
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FirebasePlanRepository: PlanRepository {
    private val firestore = Firebase.firestore
    private val planRef = firestore.collection("PLANS")
    private fun userPlanRef(userId: String) = firestore.collection("USERS").document(userId).collection("PLANS")
    private fun userWorkoutPlanRef(userId: String) = firestore.collection("USERS").document(userId).collection("WORKOUT_PLANS")
    private val context = Dispatchers.IO

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

    override suspend fun saveCustomPlan(userId: String, plan: Plan) {
        withContext(context) {
            val id = userPlanRef(userId).document.id
            userPlanRef(userId).document(id).set(plan.copy(id = id))
        }
    }

    override suspend fun saveCustomWorkout(userId: String, workoutPlan: WorkoutPlan) {
        withContext(context) {
            val id = userWorkoutPlanRef(userId).document.id
            userWorkoutPlanRef(userId).document(id).set(workoutPlan.copy())
        }
    }

    override fun getCustomWorkouts(userId: String): Flow<List<WorkoutPlan>> {
        return userWorkoutPlanRef(userId).snapshots.map { snapshot ->
            snapshot.documents.map { it.data<WorkoutPlan>() }
        }
    }

    override suspend fun deleteCustomPlan(userId: String, planId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCustomPlans(userId: String) {
        TODO("Not yet implemented")
    }
}