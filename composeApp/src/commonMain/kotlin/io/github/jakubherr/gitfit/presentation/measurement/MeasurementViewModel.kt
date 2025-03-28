package io.github.jakubherr.gitfit.presentation.measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.data.repository.FirebaseAuthRepository
import io.github.jakubherr.gitfit.domain.MeasurementRepository
import io.github.jakubherr.gitfit.domain.model.Measurement
import kotlinx.coroutines.launch

class MeasurementViewModel(
    private val measurementRepository: MeasurementRepository,
    private val authRepository: FirebaseAuthRepository,
) : ViewModel() {
    fun onAction(action: MeasurementAction) {
        when (action) {
            is MeasurementAction.SaveMeasurement -> saveMeasurement(action.measurement)
        }
    }

    private fun saveMeasurement(measurement: Measurement) {
        val user = authRepository.currentUser
        if (!user.loggedIn) return

        viewModelScope.launch {
            measurementRepository.addMeasurement(user.id, measurement)
        }
    }
}

sealed interface MeasurementAction {
    class SaveMeasurement(val measurement: Measurement) : MeasurementAction
}
