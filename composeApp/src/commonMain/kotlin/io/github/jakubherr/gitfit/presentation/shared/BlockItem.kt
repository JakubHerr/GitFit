package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.add_set
import gitfit.composeapp.generated.resources.delete
import gitfit.composeapp.generated.resources.delete_series
import gitfit.composeapp.generated.resources.done
import io.github.jakubherr.gitfit.domain.isNonNegativeDouble
import io.github.jakubherr.gitfit.domain.isNonNegativeLong
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
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
    seriesAction: String = stringResource(Res.string.done),
    onAddSet: () -> Unit = {},
) {
    Card(modifier.sizeIn(maxWidth = 512.dp).fillMaxWidth().padding(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Text(block.exercise.name, style = MaterialTheme.typography.titleLarge)
                }

                dropdownMenu()
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SetHeader(seriesAction = seriesAction)
                seriesItems()
            }

            Spacer(Modifier.height(8.dp))

            if (!readOnly) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = onAddSet,
                        Modifier.sizeIn(maxWidth = 320.dp).fillMaxWidth().testTag("AddSeriesButton"),
                    ) {
                        Text(stringResource(Res.string.add_set))
                    }
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
        seriesAction = stringResource(Res.string.delete),
        seriesItems = {
            block.series.forEachIndexed { seriesIdx, series ->
                SetInput(
                    seriesIdx,
                    series,
                    Modifier,
                    validator = { weight, reps -> weight.isNonNegativeDouble() && reps.isNonNegativeLong() },
                    onValidSetEntered = { onValidSetEntered(it) },
                    actionSlot = {
                        IconButton(
                            onClick = { onDeleteSeries(series) },
                            modifier = Modifier.testTag("WorkoutPlanDeleteSeries")
                        ) {
                            Icon(Icons.Default.Delete, stringResource(Res.string.delete_series), tint = MaterialTheme.colorScheme.error)
                        }
                    },
                )
            }
        },
    )
}

@Composable
fun WorkoutBlockItem(
    workout: Workout,
    block: Block,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    onAction: (WorkoutAction) -> Unit = {},
    onAddSetClicked: () -> Unit = {},
    dropdownMenu: @Composable () -> Unit = {},
) {
    SharedBlockItem(
        block,
        modifier = modifier.testTag("WorkoutBlockItem"),
        readOnly = readOnly,
        seriesItems = {
            block.series.forEachIndexed { seriesIdx, series ->

                if (readOnly) {
                    ReadOnlySet(seriesIdx, series)
                } else {
                    CheckableSetInput(
                        seriesIdx,
                        series,
                        onToggle = { weight, reps ->
                            onAction(
                                WorkoutAction.ModifySeries(
                                    workout,
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
        dropdownMenu = dropdownMenu,
    )
}
