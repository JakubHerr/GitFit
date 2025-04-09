package io.github.jakubherr.gitfit.presentation.measurement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.add_todays_measurement
import gitfit.composeapp.generated.resources.chest
import gitfit.composeapp.generated.resources.edit_todays_measurement
import gitfit.composeapp.generated.resources.height
import gitfit.composeapp.generated.resources.latest_measurements
import gitfit.composeapp.generated.resources.left_arm
import gitfit.composeapp.generated.resources.left_calf
import gitfit.composeapp.generated.resources.left_forearm
import gitfit.composeapp.generated.resources.left_thigh
import gitfit.composeapp.generated.resources.measurement_graph
import gitfit.composeapp.generated.resources.neck
import gitfit.composeapp.generated.resources.right_arm
import gitfit.composeapp.generated.resources.right_calf
import gitfit.composeapp.generated.resources.right_forearm
import gitfit.composeapp.generated.resources.right_thigh
import gitfit.composeapp.generated.resources.save_measurement
import gitfit.composeapp.generated.resources.see_all
import gitfit.composeapp.generated.resources.waist
import gitfit.composeapp.generated.resources.weight
import io.github.jakubherr.gitfit.domain.model.Measurement
import io.github.jakubherr.gitfit.presentation.graph.MeasurementLineGraph
import io.github.jakubherr.gitfit.presentation.shared.DoubleInputField
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.enums.EnumEntries

enum class MeasurementType(
    val label: StringResource,
    val unit: String = "cm",
    val backingField: MutableState<String> = mutableStateOf(""),
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
    WEIGHT(Res.string.weight, "kg"),
    HEIGHT(Res.string.height),
    ;

    fun value() = backingField.value.toDoubleOrNull()

    fun setInitialValue(value: String) {
        backingField.value = value
    }
}

// use case: add, edit and review body measurements over time
@Composable
fun MeasurementScreenRoot(
    vm: MeasurementViewModel,
    modifier: Modifier = Modifier,
    onRequestAddEditMeasurement: () -> Unit = {},
    onShowHistory: () -> Unit = {},
) {
    val todaysMeasurement by vm.todayMeasurement.collectAsStateWithLifecycle(null)
    val measurements by vm.allUserMeasurements.collectAsStateWithLifecycle(emptyList())
    var selectedMeasurementType by remember { mutableStateOf(MeasurementType.CHEST) }

    Column(
        modifier.fillMaxSize().padding(16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(Res.string.measurement_graph))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(selectedMeasurementType.label))

                MeasurementSelectionDropdown {
                    selectedMeasurementType = it
                }
            }
        }

        MeasurementLineGraph(
            measurements,
            selectedMeasurementType,
            modifier = Modifier.fillMaxHeight(0.5f),
        )

        Row(
            modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(Res.string.latest_measurements))
            TextButton(onShowHistory) {
                Text(stringResource(Res.string.see_all))
            }
        }

        LazyColumn(
            modifier.weight(1.0f),
        ) {
            items(measurements) { measurement ->
                Text(measurement.date.toString())
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            val add = stringResource(Res.string.add_todays_measurement)
            val edit = stringResource(Res.string.edit_todays_measurement)

            Button(onRequestAddEditMeasurement) {
                val text = if (todaysMeasurement == null) add else edit
                Text(text)
            }
        }
    }
}

@Composable
fun AddEditMeasurement(
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
            modifier.fillMaxSize().weight(1.0f),
        ) {
            items(measurementTypes) { measurement ->
                MeasurementInputField(
                    measurement.backingField.value,
                    measurement.label,
                    measurement.unit,
                    Modifier.padding(16.dp),
                ) {
                    measurement.backingField.value = it
                }
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(onClick = {
                // TODO notify of error
                val measurement = measurementTypes.toMeasurement()
                onSave(measurement)
            }) {
                Text(stringResource(Res.string.save_measurement))
            }
        }
    }
}

@Composable
fun MeasurementInputField(
    value: String,
    label: StringResource?,
    unit: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        label?.let { Text(stringResource(it)) }

        Row(
            Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DoubleInputField(value, onValueChange = onValueChange)
            Spacer(Modifier.width(12.dp))
            Text(unit)
        }
    }
}

@Composable
fun MeasurementSelectionDropdown(
    modifier: Modifier = Modifier,
    onSelected: (MeasurementType) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton({ expanded = !expanded }) {
        Icon(Icons.Default.KeyboardArrowDown, "")
    }

    DropdownMenu(
        expanded,
        onDismissRequest = { expanded = false },
    ) {
        MeasurementType.entries.forEach { type ->
            DropdownMenuItem(
                text = { Text(stringResource(type.label)) },
                onClick = {
                    onSelected(type)
                    expanded = false
                },
            )
        }
    }
}

private fun EnumEntries<MeasurementType>.toMeasurement() =
    Measurement(
        date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
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
