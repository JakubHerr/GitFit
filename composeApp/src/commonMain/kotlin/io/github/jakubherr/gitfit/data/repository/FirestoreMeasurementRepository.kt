package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.model.Measurement
import io.github.jakubherr.gitfit.domain.repository.AuthError
import io.github.jakubherr.gitfit.domain.repository.MeasurementRepository
import io.github.jakubherr.gitfit.domain.today
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FirestoreMeasurementRepository : MeasurementRepository {
    private fun measurementsRef(userId: String) = Firebase.firestore.collection("USERS").document(userId).collection("MEASUREMENTS")

    private val dispatcher = Dispatchers.IO

    override fun userMeasurementFlow(userId: String): Flow<List<Measurement>> {
        if (userId.isBlank()) return emptyFlow()

        return measurementsRef(userId).snapshots.map { list ->
            list.documents.mapNotNull {
                runCatching { it.data<Measurement>() }.getOrNull()
            }
        }
    }

    override fun todayMeasurementFlow(userId: String): Flow<Measurement?> {
        if (userId.isBlank()) return emptyFlow()

        return measurementsRef(userId).document(today().toString()).snapshots.map {
            runCatching { it.data<Measurement>() }.getOrNull()
        }
    }

    override suspend fun saveMeasurement(
        userId: String,
        measurement: Measurement,
    ): Result<Unit> {
        return withContext(dispatcher) {
            runCatching { measurementsRef(userId).document(today().toString()).set(measurement) }
        }
    }

    override suspend fun deleteMeasurement(
        userId: String,
        measurement: Measurement,
    ): Result<Unit> {
        return withContext(dispatcher) {
            runCatching { measurementsRef(userId).document(measurement.date.toString()).delete() }
        }
    }

    override suspend fun deleteAllMeasurements(userId: String): Result<Unit> {
        userId.ifBlank { return Result.failure(AuthError.UserLoggedOut) }

        return withContext(dispatcher) {
            measurementsRef(userId).get().documents.forEach { document ->
                try {
                    measurementsRef(userId).document(document.id).delete()
                } catch (e: Exception) {
                    return@withContext Result.failure(e)
                }
            }
            Result.success(Unit)
        }
    }
}
