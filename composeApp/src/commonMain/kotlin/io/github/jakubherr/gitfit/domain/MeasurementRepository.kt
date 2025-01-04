package io.github.jakubherr.gitfit.domain

import io.github.jakubherr.gitfit.domain.model.Measurement

interface MeasurementRepository {
    suspend fun getAllMeasurements(userId: String): List<Measurement>

    suspend fun addMeasurement(measurement: Measurement)

    suspend fun deleteMeasurement(measurementId: String)

    suspend fun deleteAll(userId: String)
}
