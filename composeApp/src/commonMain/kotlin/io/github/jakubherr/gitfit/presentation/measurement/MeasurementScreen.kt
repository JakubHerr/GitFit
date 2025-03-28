package io.github.jakubherr.gitfit.presentation.measurement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.chest
import gitfit.composeapp.generated.resources.height
import gitfit.composeapp.generated.resources.left_arm
import gitfit.composeapp.generated.resources.left_calf
import gitfit.composeapp.generated.resources.left_forearm
import gitfit.composeapp.generated.resources.left_thigh
import gitfit.composeapp.generated.resources.neck
import gitfit.composeapp.generated.resources.right_arm
import gitfit.composeapp.generated.resources.right_calf
import gitfit.composeapp.generated.resources.right_forearm
import gitfit.composeapp.generated.resources.right_thigh
import gitfit.composeapp.generated.resources.save_measurement
import gitfit.composeapp.generated.resources.waist
import gitfit.composeapp.generated.resources.weight
import io.github.jakubherr.gitfit.domain.isPositiveDouble
import io.github.jakubherr.gitfit.domain.model.Measurement
import io.github.jakubherr.gitfit.presentation.workout.NumberInputField
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

enum class MeasurementType(
    val label: StringResource,
    val backingField: MutableState<String> = mutableStateOf(""),
    val minValue: Double = 0.0,
) {
    NECK(Res.string.neck),
    CHEST(Res.string.chest),
    LEFT_ARM(Res.string.left_arm),
    RIGHT_ARM(Res.string.right_arm),
    LEFT_FOREARM(Res.string.left_forearm),
    RIGHT_FOREARM(Res.string.right_forearm),
    WAIST(Res.string.waist),
    LEFT_THIGH(Res.string.left_thigh),
    RIGHT_THIGH(Res.string.right_thigh),
    LEFT_CALF(Res.string.left_calf),
    RIGHT_CALF(Res.string.right_calf),
    WEIGHT(Res.string.weight),
    HEIGHT(Res.string.height),
    ;

    fun value() = backingField.value.toDoubleOrNull()
}

// use case: add, edit and review body measurements over time
@Composable
fun MeasurementScreenRoot(
    vm: MeasurementViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val measurements = remember { MeasurementType.entries }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        measurements.forEach { measurement ->
            MeasurementInputField(measurement.backingField.value, measurement.label) {
                measurement.backingField.value = it
            }
        }
        Button(onClick = {
            // TODO more validations, allow missing values, notify of error
            val isInputValid = measurements.all { it.backingField.value.isPositiveDouble() }
            if (!isInputValid) return@Button

            val measurement =
                Measurement(
                    date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    measurements[0].value(),
                    measurements[1].value(),
                    measurements[2].value(),
                    measurements[3].value(),
                    measurements[4].value(),
                    measurements[5].value(),
                    measurements[6].value(),
                    measurements[7].value(),
                    measurements[8].value(),
                    measurements[9].value(),
                    measurements[10].value(),
                    measurements[11].value(),
                    measurements[12].value(),
                )

            vm.onAction(MeasurementAction.SaveMeasurement(measurement))
        }) {
            Text(stringResource(Res.string.save_measurement))
        }
    }
}

@Composable
fun MeasurementInputField(
    value: String,
    label: StringResource?,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        label?.let { Text(stringResource(it)) }
        Spacer(Modifier.width(8.dp))
        NumberInputField(value, onValueChange = onValueChange)
    }
}
