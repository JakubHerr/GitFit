package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.jakubherr.gitfit.domain.isNonNegativeDouble
import io.github.jakubherr.gitfit.domain.isNonNegativeInt
import io.github.jakubherr.gitfit.domain.model.Series

@Composable
fun SetInput(
    index: Int,
    set: Series,
    modifier: Modifier = Modifier,
    validator: (String, String) -> Boolean,
    onValidSetEntered: (Series) -> Unit = {},
    actionSlot: @Composable () -> Unit = {},
) {
    var weight by remember(set) { mutableStateOf(set.weight?.toPrettyString() ?: "") }
    var reps by remember(set) { mutableStateOf(set.repetitions?.toString() ?: "") }

    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text((index + 1).toString())

        DoubleInputField(
            weight,
            onValueChange = { newWeight ->
                weight = newWeight

                if (validator(weight, reps)) {
                    onValidSetEntered(set.copy(weight = weight.toDouble(), repetitions = reps.toLong()))
                }
            },
        )

        IntegerInputField(
            reps,
            onValueChange = {
                reps = it

                if (validator(weight, reps)) {
                    onValidSetEntered(set.copy(weight = weight.toDouble(), repetitions = reps.toLong()))
                }
            },
        )

        actionSlot()
    }
}

@Composable
fun CheckableSetInput(
    index: Int,
    set: Series,
    modifier: Modifier = Modifier,
    onToggle: (String, String) -> Unit,
) {
    var weight by remember { mutableStateOf(set.weight?.toPrettyString() ?: "") }
    var reps by remember { mutableStateOf(set.repetitions?.toString() ?: "") }

    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text((index + 1).toString())

        DoubleInputField(weight, onValueChange = { weight = it }, enabled = !set.completed)
        IntegerInputField(reps, onValueChange = { reps = it }, enabled = !set.completed)

        Checkbox(
            set.completed,
            enabled = weight.isNonNegativeDouble() && reps.isNonNegativeInt(),
            onCheckedChange = { onToggle(weight, reps) },
        )
    }
}

@Composable
fun ReadOnlySet(
    index: Int,
    set: Series,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text((index + 1).toString())

        Text(set.weight?.toPrettyString() ?: "0")
        Text(set.repetitions.toString())

        Checkbox(
            set.completed,
            enabled = false,
            onCheckedChange = { },
        )
    }
}
