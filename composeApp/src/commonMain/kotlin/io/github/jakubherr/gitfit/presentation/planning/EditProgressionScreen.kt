package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.cancel
import gitfit.composeapp.generated.resources.delete
import gitfit.composeapp.generated.resources.edit_progression
import gitfit.composeapp.generated.resources.enum_progression_type_icrease_reps
import gitfit.composeapp.generated.resources.enum_progression_type_icrease_weight
import gitfit.composeapp.generated.resources.minimum_repetitions
import gitfit.composeapp.generated.resources.minimum_weight
import gitfit.composeapp.generated.resources.progresison_type
import gitfit.composeapp.generated.resources.progression_explanation_1
import gitfit.composeapp.generated.resources.progression_explanation_2
import gitfit.composeapp.generated.resources.repetition_increase
import gitfit.composeapp.generated.resources.repetitions
import gitfit.composeapp.generated.resources.save
import gitfit.composeapp.generated.resources.starting_values
import gitfit.composeapp.generated.resources.weight
import gitfit.composeapp.generated.resources.weight_increase
import io.github.jakubherr.gitfit.domain.isPositiveDouble
import io.github.jakubherr.gitfit.domain.isPositiveInt
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.ProgressionSettings
import io.github.jakubherr.gitfit.domain.model.ProgressionTrigger
import io.github.jakubherr.gitfit.domain.model.ProgressionType
import io.github.jakubherr.gitfit.presentation.shared.DoubleInputField
import io.github.jakubherr.gitfit.presentation.shared.SingleChoiceChipSelection
import io.github.jakubherr.gitfit.presentation.shared.toPrettyString
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditProgressionScreen(
    block: Block,
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSave: (ProgressionSettings) -> Unit = {},
) {
    val initialWeight = block.progressionSettings?.weightThreshold.let { it?.toPrettyString() ?: "" }
    val initialReps = block.progressionSettings?.repThreshold ?: 12

    var selectedProgressionType by remember { mutableStateOf(ProgressionType.INCREASE_WEIGHT) }
    var minimumReps by remember { mutableStateOf(initialReps) }
    var minimumWeight by remember { mutableStateOf(initialWeight) }

    var weightIncrease by remember { mutableStateOf("") }
    var repIncrease by remember { mutableStateOf("") }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        Column(Modifier.weight(1.0f)) {
            Text("${stringResource(Res.string.edit_progression)} ${block.exercise.name}")
            Text(stringResource(Res.string.starting_values))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(Res.string.minimum_weight))
                DoubleInputField(
                    minimumWeight,
                    onValueChange = { minimumWeight = it },
                )
            }

            Row {
                Text(stringResource(Res.string.minimum_repetitions))
                MinimumRepSelector(minimumReps) { minimumReps = it }
            }

            Text(stringResource(Res.string.progresison_type))

            val translations =
                listOf(
                    stringResource(Res.string.enum_progression_type_icrease_weight),
                    stringResource(Res.string.enum_progression_type_icrease_reps),
                )
            SingleChoiceChipSelection(ProgressionType.entries, translations, selectedProgressionType) {
                selectedProgressionType = it
            }

            if (selectedProgressionType == ProgressionType.INCREASE_WEIGHT) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(Res.string.weight_increase))
                    DoubleInputField(
                        weightIncrease,
                        isError = !weightIncrease.isPositiveDouble(),
                        onValueChange = { weightIncrease = it },
                    )
                }
            }

            if (selectedProgressionType == ProgressionType.INCREASE_REPS) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(Res.string.repetition_increase))
                    DoubleInputField(
                        repIncrease,
                        isError = !repIncrease.isPositiveInt(),
                        onValueChange = { repIncrease = it },
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            ProgressionHint(selectedProgressionType, minimumWeight, weightIncrease, repIncrease, minimumReps)
        }

        Row(
            modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(
                enabled = validateInputs(selectedProgressionType, minimumWeight, weightIncrease, repIncrease),
                onClick = {
                    val settings =
                        ProgressionSettings(
                            incrementWeightByKg = weightIncrease.toDoubleOrNull() ?: 0.0,
                            incrementRepsBy = repIncrease.toIntOrNull() ?: 0,
                            type = selectedProgressionType,
                            weightThreshold = minimumWeight.toDouble(),
                            repThreshold = minimumReps,
                            trigger = ProgressionTrigger.MINIMUM_REPS_AND_WEIGHT_EVERY_SET,
                        )

                    onSave(settings)
                },
            ) {
                Text(stringResource(Res.string.save))
            }
            Button(onCancel) {
                Text(stringResource(Res.string.cancel))
            }

            if (block.progressionSettings != null) {
                Button(onDelete) {
                    Text(stringResource(Res.string.delete))
                }
            }
        }
    }
}

@Composable
fun ProgressionHint(
    selectedProgressionType: ProgressionType,
    minimumWeight: String,
    weightIncrease: String,
    repIncrease: String,
    minimumReps: Int,
) {
    if (validateInputs(selectedProgressionType, minimumWeight, weightIncrease, repIncrease)) {
        val value =
            if (selectedProgressionType == ProgressionType.INCREASE_WEIGHT) {
                stringResource(Res.string.weight).lowercase()
            } else {
                stringResource(Res.string.repetitions).lowercase()
            }

        val increment =
            if (selectedProgressionType == ProgressionType.INCREASE_WEIGHT) "$weightIncrease kg" else repIncrease

        val string1 = stringResource(Res.string.progression_explanation_1, minimumReps, minimumWeight.toDouble())
        val string2 = stringResource(Res.string.progression_explanation_2, value, increment)
        Text(string1 + string2)
    }
}

@Composable
fun MinimumRepSelector(
    reps: Int,
    modifier: Modifier = Modifier,
    onRepChange: (Int) -> Unit = {},
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton({ if (reps > 0) onRepChange(reps - 1) }) {
            Icon(Icons.Default.Remove, "")
        }
        Text(reps.toString())
        IconButton({ onRepChange(reps + 1) }) {
            Icon(Icons.Default.Add, "")
        }
    }
}

private fun validateInputs(
    type: ProgressionType,
    minimumWeight: String,
    weightIncrease: String,
    repIncrease: String,
): Boolean {
    val validProgression =
        when (type) {
            ProgressionType.INCREASE_WEIGHT -> weightIncrease.isPositiveDouble()
            ProgressionType.INCREASE_REPS -> repIncrease.isPositiveInt()
        }
    return validProgression && minimumWeight.isPositiveDouble()
}
