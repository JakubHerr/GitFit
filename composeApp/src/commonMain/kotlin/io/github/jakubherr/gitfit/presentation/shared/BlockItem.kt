package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.add_set
import io.github.jakubherr.gitfit.domain.isNonNegativeDouble
import io.github.jakubherr.gitfit.domain.isNonNegativeLong
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.presentation.planning.PlanBlockItemDropdownMenu
import io.github.jakubherr.gitfit.presentation.workout.SetHeader
import io.github.jakubherr.gitfit.presentation.workout.WorkoutAction
import org.jetbrains.compose.resources.stringResource

@Composable
fun SharedBlockItem(
    block: Block,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    dropdownMenu: @Composable () -> Unit = {},
    seriesItems: @Composable () -> Unit = {},
    onAddSet: () -> Unit = {},
) {
    Card(modifier.fillMaxWidth().padding(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(Modifier.weight(1.0f)) {
                    Text(block.exercise.name, style = MaterialTheme.typography.titleLarge)
                }

                dropdownMenu()
            }

            HorizontalDivider(Modifier.padding(8.dp))

            Column {
                SetHeader()
                Spacer(Modifier.height(16.dp))
                seriesItems()
            }

            Spacer(Modifier.height(8.dp))

            if (!readOnly) {
                Button(
                    onClick = onAddSet,
                    Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.add_set))
                }
            }
        }
    }
}

@Composable
fun PlanBlockItem(
    block: Block,
    modifier: Modifier = Modifier,
    onAddSetClicked: () -> Unit = {},
    onValidSetEntered: (Series) -> Unit = {},
    onDeleteSeries: (Series) -> Unit = {},
    onDeleteExercise: () -> Unit = {},
    onEditProgression: () -> Unit = {},
) {
    SharedBlockItem(
        block = block,
        modifier = modifier,
        onAddSet = onAddSetClicked,
        dropdownMenu = {
            PlanBlockItemDropdownMenu(
                onDeleteExercise = onDeleteExercise,
                onEditProgression = onEditProgression,
            )
        },
        seriesItems = {
            block.series.forEachIndexed { seriesIdx, series ->
                SetInput(
                    seriesIdx,
                    series,
                    Modifier,
                    validator = { weight, reps -> weight.isNonNegativeDouble() && reps.isNonNegativeLong() },
                    onValidSetEntered = { onValidSetEntered(it) },
                    actionSlot = {
                        IconButton({ onDeleteSeries(series) }) {
                            Icon(Icons.Default.Delete, "")
                        }
                    }
                )
            }
        }
    )
}

@Composable
fun WorkoutBlockItem(
    block: Block,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    onAction: (WorkoutAction) -> Unit = {},
    onAddSetClicked: () -> Unit = {},
    dropdownMenu: @Composable () -> Unit = {}
) {
    SharedBlockItem(
        block,
        modifier = modifier,
        readOnly = readOnly,
        seriesItems = {
            block.series.forEachIndexed { seriesIdx, series ->

                if (readOnly) {
                    ReadOnlySet(seriesIdx, series)
                } else{
                    CheckableSetInput(
                        seriesIdx,
                        series,
                        onToggle = { weight, reps ->
                            onAction(
                                WorkoutAction.ModifySeries(
                                    block.idx,
                                    series.copy(
                                        weight = weight.toDouble(),
                                        repetitions = reps.toLong(),
                                        completed = !series.completed,
                                    ),
                                ),
                            )
                        },
                    )
                }
            }
        },
        onAddSet = onAddSetClicked,
        dropdownMenu = dropdownMenu
    )
}
