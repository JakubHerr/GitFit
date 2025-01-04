package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.MeasurementRepository
import io.github.jakubherr.gitfit.domain.model.Measurement

class FirestoreMeasurementRepository : MeasurementRepository {
    private val auth = Firebase.auth
    private val measurementsRef = Firebase.firestore.collection("MEASUREMENTS")

    override suspend fun getAllMeasurements(userId: String): List<Measurement> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        return measurementsRef.where {
            "userId" equalTo uid
        }.get().documents.map { it.data<Measurement>() }
    }

    override suspend fun addMeasurement(measurement: Measurement) {
        val uid = auth.currentUser?.uid ?: return
        val id = measurementsRef.document.id
        measurementsRef.document(id).set(measurement.copy(id = id, userId = uid))
    }

    override suspend fun deleteMeasurement(measurementId: String) {
        measurementsRef.document(measurementId).delete()
    }

    override suspend fun deleteAll(userId: String) {
        TODO("Not yet implemented")
    }
}
