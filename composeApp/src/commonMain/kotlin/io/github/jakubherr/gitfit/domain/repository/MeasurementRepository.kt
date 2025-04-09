package io.github.jakubherr.gitfit.domain.repository

import io.github.jakubherr.gitfit.domain.model.Measurement
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {
    fun userMeasurementFlow(userId: String) : Flow<List<Measurement>>

    fun todayMeasurementFlow(userId: String): Flow<Measurement?>

    suspend fun saveMeasurement(userId: String, measurement: Measurement): Result<Unit>

    suspend fun deleteMeasurement(userId: String, measurement: Measurement): Result<Unit>

    suspend fun deleteAllMeasurements(userId: String): Result<Unit>
}
