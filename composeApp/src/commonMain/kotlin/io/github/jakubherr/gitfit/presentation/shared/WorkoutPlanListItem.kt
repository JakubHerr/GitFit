package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.click_to_add_exercise
import gitfit.composeapp.generated.resources.delete_series
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import org.jetbrains.compose.resources.stringResource

@Composable
fun WorkoutPlanListItem(
    workout: WorkoutPlan,
    modifier: Modifier = Modifier,
    onItemClicked: () -> Unit = {},
    actionSlot: @Composable () -> Unit = { Icon(Icons.Default.Delete, stringResource(Res.string.delete_series), tint = MaterialTheme.colorScheme.error) },
    onActionClicked: () -> Unit = {},
) {
    Card(
        onItemClicked,
        modifier,
    ) {
        Column(
            Modifier.padding(8.dp),
        ) {
            Row(
                modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(workout.name, fontWeight = FontWeight.Bold)

                IconButton(
                    onActionClicked,
                    modifier = Modifier.testTag("WorkoutPlanListItemAction")
                ) {
                    actionSlot()
                }
            }

            Spacer(Modifier.height(4.dp))

            Row {
                ExerciseNames(workout.blocks)
            }
        }
    }
}

@Composable
fun ExerciseNames(
    blockList: List<Block>,
    modifier: Modifier = Modifier,
) {
    Text(
        if (blockList.isNotEmpty()) blockList.joinToString { it.exercise.name } else stringResource(Res.string.click_to_add_exercise),
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
    )
}
