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
import io.github.jakubherr.gitfit.domain.isPositiveDouble
import io.github.jakubherr.gitfit.domain.isPositiveInt
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.ProgressionSettings
import io.github.jakubherr.gitfit.domain.model.ProgressionTrigger
import io.github.jakubherr.gitfit.domain.model.ProgressionType
import io.github.jakubherr.gitfit.presentation.shared.DoubleInputField
import io.github.jakubherr.gitfit.presentation.shared.SingleChoiceChipSelection

@Composable
fun EditProgressionScreenRoot(
    block: Block,
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSave: (ProgressionSettings) -> Unit = {},
) {
    val initialWeight = block.progressionSettings?.weightThreshold.let { it?.toString() ?: "" }
    val initialReps = block.progressionSettings?.repThreshold ?: 12

    var selectedProgressionType by remember { mutableStateOf(ProgressionType.INCREASE_WEIGHT) }
    var minimumReps by remember { mutableStateOf(initialReps) }
    var minimumWeight by remember { mutableStateOf(initialWeight) }

    var weightIncrease by remember { mutableStateOf("") }
    var repIncrease by remember { mutableStateOf("") }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        Column(Modifier.weight(1.0f)) {
            Text("Edit progression for: ${block.exercise.name}")
            Text("Starting values:")

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Minimum weight (kg)")
                DoubleInputField(
                    minimumWeight,
                    onValueChange = { minimumWeight = it }
                )
            }

            Row {
                Text("Minimum repetitions")
                MinimumRepSelector(minimumReps) { minimumReps = it }
            }

            Text("Progression type: ")
            SingleChoiceChipSelection(ProgressionType.entries, selectedProgressionType) {
                selectedProgressionType = it
            }

            if (selectedProgressionType == ProgressionType.INCREASE_WEIGHT) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Weight increase: ")
                    DoubleInputField(
                        weightIncrease,
                        isError = !weightIncrease.isPositiveDouble(),
                        onValueChange = { weightIncrease = it }
                    )
                }
            }

            if (selectedProgressionType == ProgressionType.INCREASE_REPS) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Repetition increase: ")
                    DoubleInputField(
                        repIncrease,
                        isError = !repIncrease.isPositiveInt(),
                        onValueChange = { repIncrease = it }
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
                    // TODO test
                    val settings = ProgressionSettings(
                        incrementWeightByKg = weightIncrease.toDoubleOrNull() ?: 0.0,
                        incrementRepsBy = repIncrease.toIntOrNull() ?: 0,
                        type = selectedProgressionType,
                        weightThreshold = minimumWeight.toDouble(),
                        repThreshold = minimumReps,
                        trigger = ProgressionTrigger.MINIMUM_REPS_AND_WEIGHT_EVERY_SET
                    )

                    println("DBG: saving progression: $settings")
                    onSave(settings)
                }
            ) {
                Text("Save")
            }
            Button(onCancel) {
                Text("Cancel")
            }

            if (block.progressionSettings != null) {
                Button(onDelete) {
                    Text("Delete")
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
    minimumReps: Int
) {
    if (validateInputs(selectedProgressionType, minimumWeight, weightIncrease, repIncrease)) {
        val text =
            "Progression will start when you reach $minimumReps repetitions with a weight of ${minimumWeight.toDouble()} kg every set."
        val value = if (selectedProgressionType == ProgressionType.INCREASE_WEIGHT) "weight" else "repetitions"
        val increment =
            if (selectedProgressionType == ProgressionType.INCREASE_WEIGHT) "$weightIncrease kg" else repIncrease
        val text2 = " After every successful workout, the $value will increase by $increment"

        Text(text + text2)
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
    val validProgression = when (type) {
        ProgressionType.INCREASE_WEIGHT -> weightIncrease.isPositiveDouble()
        ProgressionType.INCREASE_REPS -> repIncrease.isPositiveInt()
    }
    return validProgression && minimumWeight.isPositiveDouble()
}