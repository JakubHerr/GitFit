package io.github.jakubherr.gitfit.presentation.measurement

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.cancel
import gitfit.composeapp.generated.resources.chest
import gitfit.composeapp.generated.resources.delete
import gitfit.composeapp.generated.resources.delete_measurement
import gitfit.composeapp.generated.resources.delete_measurement_explanation
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
import gitfit.composeapp.generated.resources.waist
import gitfit.composeapp.generated.resources.weight
import io.github.jakubherr.gitfit.domain.model.Measurement
import io.github.jakubherr.gitfit.presentation.shared.ConfirmationDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun MeasurementHistoryScreen(
    measurements: List<Measurement>,
    modifier: Modifier = Modifier,
    onDeleteMeasurement: (Measurement) -> Unit = {},
) {
    Column(
        Modifier.padding(16.dp).fillMaxSize(),
    ) {
        MeasurementList(
            measurements,
            onDeleteMeasurement = {
                onDeleteMeasurement(it)
            },
        )
    }
}

@Composable
fun MeasurementList(
    measurements: List<Measurement>,
    modifier: Modifier = Modifier,
    onDeleteMeasurement: (Measurement) -> Unit = {},
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(measurements) { measurement ->
            MeasurementListItem(measurement) {
                onDeleteMeasurement(it)
            }
        }
    }
}

@Composable
fun MeasurementListItem(
    measurement: Measurement,
    modifier: Modifier = Modifier,
    onDeleteMeasurement: (Measurement) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        ConfirmationDialog(
            title = stringResource(Res.string.delete_measurement),
            text = stringResource(Res.string.delete_measurement_explanation),
            dismissText = stringResource(Res.string.cancel),
            onDismiss = { showDialog = false },
            confirmText = stringResource(Res.string.delete),
            onConfirm = {
                onDeleteMeasurement(measurement)
                showDialog = false
            },
        )
    }

    Card(
        modifier = modifier,
        onClick = { expanded = !expanded },
    ) {
        Column(
            Modifier.fillMaxSize().animateContentSize().padding(8.dp),
        ) {
            Row(
                Modifier.fillMaxWidth().sizeIn(minHeight = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(measurement.date.toString(), fontWeight = FontWeight.Bold)

                val icon = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
                Icon(icon, "")
            }

            if (expanded) {
                Text("${stringResource(Res.string.neck)}: ${measurement.neck} cm")
                Text("${stringResource(Res.string.chest)}: ${measurement.chest} cm")
                Text("${stringResource(Res.string.left_arm)}: ${measurement.leftArm} cm")
                Text("${stringResource(Res.string.right_arm)}: ${measurement.rightArm} cm")
                Text("${stringResource(Res.string.left_forearm)}: ${measurement.leftForearm} cm")
                Text("${stringResource(Res.string.right_forearm)}: ${measurement.rightForearm} cm")
                Text("${stringResource(Res.string.waist)}: ${measurement.waist} cm")
                Text("${stringResource(Res.string.left_thigh)}: ${measurement.leftThigh} cm")
                Text("${stringResource(Res.string.right_thigh)}: ${measurement.rightThigh} cm")
                Text("${stringResource(Res.string.left_calf)}: ${measurement.leftCalf} cm")
                Text("${stringResource(Res.string.right_calf)}: ${measurement.rightCalf} cm")
                Text("${stringResource(Res.string.weight)}: ${measurement.bodyweight} kg")
                Text("${stringResource(Res.string.height)}: ${measurement.height} cm")

                Spacer(Modifier.height(8.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError,
                            ),
                    ) {
                        Text(stringResource(Res.string.delete_measurement))
                    }
                }
            }
        }
    }
}
