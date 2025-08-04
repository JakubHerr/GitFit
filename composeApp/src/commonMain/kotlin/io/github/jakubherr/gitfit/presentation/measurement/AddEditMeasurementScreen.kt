package io.github.jakubherr.gitfit.presentation.measurement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.save_measurement
import io.github.jakubherr.gitfit.domain.model.Measurement
import io.github.jakubherr.gitfit.domain.today
import org.jetbrains.compose.resources.stringResource
import kotlin.enums.EnumEntries

@Composable
fun AddEditMeasurementScreen(
    modifier: Modifier = Modifier,
    oldMeasurement: Measurement?,
    onSave: (Measurement) -> Unit = {},
) {
    val measurementTypes = remember { MeasurementType.entries }

    LaunchedEffect(oldMeasurement) {
        if (oldMeasurement != null) {
            measurementTypes.fromMeasurement(oldMeasurement)
        }
    }

    Column(Modifier.padding(16.dp)) {
        LazyColumn(
            modifier.fillMaxSize().weight(1.0f).testTag("MeasurementLazyList"),
        ) {
            itemsIndexed(measurementTypes) { idx, measurement ->
                MeasurementInputField(
                    measurement.backingField.value,
                    measurement.label,
                    measurement.unit,
                    Modifier.padding(16.dp),
                    imeAction = if (idx == measurementTypes.lastIndex) ImeAction.Done else ImeAction.Next,
                    testTag = "MeasurementInput$idx",
                ) {
                    measurement.backingField.value = it
                }
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = {
                    // TODO notify of error
                    val measurement = measurementTypes.toMeasurement()
                    onSave(measurement)
                },
                modifier = Modifier.testTag("SaveMeasurementButton"),
            ) {
                Text(stringResource(Res.string.save_measurement))
            }
        }
    }
}

private fun EnumEntries<MeasurementType>.toMeasurement() =
    Measurement(
        date = today(),
        get(0).value(),
        get(1).value(),
        get(2).value(),
        get(3).value(),
        get(4).value(),
        get(5).value(),
        get(6).value(),
        get(7).value(),
        get(8).value(),
        get(9).value(),
        get(10).value(),
        get(11).value(),
        get(12).value(),
    )

private fun EnumEntries<MeasurementType>.fromMeasurement(measurement: Measurement) {
    measurement.neck?.let { get(0).setInitialValue(it.toString()) }
    measurement.chest?.let { get(1).setInitialValue(it.toString()) }
    measurement.leftArm?.let { get(2).setInitialValue(it.toString()) }
    measurement.rightArm?.let { get(3).setInitialValue(it.toString()) }
    measurement.leftForearm?.let { get(4).setInitialValue(it.toString()) }
    measurement.rightForearm?.let { get(5).setInitialValue(it.toString()) }
    measurement.waist?.let { get(6).setInitialValue(it.toString()) }
    measurement.leftThigh?.let { get(7).setInitialValue(it.toString()) }
    measurement.rightThigh?.let { get(8).setInitialValue(it.toString()) }
    measurement.leftCalf?.let { get(9).setInitialValue(it.toString()) }
    measurement.rightCalf?.let { get(10).setInitialValue(it.toString()) }
    measurement.bodyweight?.let { get(11).setInitialValue(it.toString()) }
    measurement.height?.let { get(12).setInitialValue(it.toString()) }
}
