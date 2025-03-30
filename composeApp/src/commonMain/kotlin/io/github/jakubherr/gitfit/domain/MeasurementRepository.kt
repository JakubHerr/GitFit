package io.github.jakubherr.gitfit.domain

import io.github.jakubherr.gitfit.domain.model.Measurement
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {
    suspend fun getAllMeasurements(userId: String): List<Measurement>

    fun userMeasurementFlow(userId: String) : Flow<List<Measurement>>

    suspend fun addMeasurement(userId: String, measurement: Measurement)

    suspend fun deleteMeasurement(userId: String, measurementId: String)

    suspend fun deleteAll(userId: String)
}
