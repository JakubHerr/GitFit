package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.repository.MeasurementRepository
import io.github.jakubherr.gitfit.domain.model.Measurement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class FirestoreMeasurementRepository : MeasurementRepository {
    private fun measurementsRef(userId: String) = Firebase.firestore.collection("USERS").document(userId).collection("MEASUREMENTS")
    private val today get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

    override fun getAllMeasurements(userId: String): Flow<List<Measurement>> {
        if (userId.isBlank()) return emptyFlow()

        return measurementsRef(userId).snapshots.map { snapshot ->
            snapshot.documents.map { it.data<Measurement>() }
        }
    }

    override fun userMeasurementFlow(userId: String): Flow<List<Measurement>> {
        if (userId.isBlank()) return emptyFlow()
        return measurementsRef(userId).snapshots.map { list ->
            list.documents.map { it.data<Measurement>() }
        }
    }

    override fun todayMeasurementFlow(userId: String): Flow<Measurement?> {
        if (userId.isBlank()) return emptyFlow()
        return measurementsRef(userId).document(today).snapshots.map {
            if (it.exists) it.data<Measurement>() else null
        }
    }

    override suspend fun saveMeasurement(userId: String, measurement: Measurement) {
        measurementsRef(userId).document(today).set(measurement)
    }

    override suspend fun deleteMeasurement(userId: String, measurementId: String) {
        measurementsRef(userId).document(measurementId).delete()
    }

    override suspend fun deleteAll(userId: String) {
        TODO("Not yet implemented")
    }
}
