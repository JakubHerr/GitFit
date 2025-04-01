package io.github.jakubherr.gitfit.domain.repository

import io.github.jakubherr.gitfit.domain.model.Measurement
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {
    fun getAllMeasurements(userId: String): Flow<List<Measurement>>

    fun userMeasurementFlow(userId: String) : Flow<List<Measurement>>

    fun todayMeasurementFlow(userId: String): Flow<Measurement?>

    suspend fun saveMeasurement(userId: String, measurement: Measurement)

    suspend fun deleteMeasurement(userId: String, measurementId: String)

    suspend fun deleteAllMeasurements(userId: String): Result<Unit>
}
