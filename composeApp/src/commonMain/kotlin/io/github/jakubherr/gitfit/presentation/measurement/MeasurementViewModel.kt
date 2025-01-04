package io.github.jakubherr.gitfit.presentation.measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.MeasurementRepository
import io.github.jakubherr.gitfit.domain.model.Measurement
import kotlinx.coroutines.launch

class MeasurementViewModel(
    private val measurementRepository: MeasurementRepository,
) : ViewModel() {
    fun onAction(action: MeasurementAction) {
        when (action) {
            is MeasurementAction.SaveMeasurement -> saveMeasurement(action.measurement)
        }
    }

    private fun saveMeasurement(measurement: Measurement) {
        viewModelScope.launch {
            measurementRepository.addMeasurement(measurement)
        }
    }
}

sealed interface MeasurementAction {
    class SaveMeasurement(val measurement: Measurement) : MeasurementAction
}
