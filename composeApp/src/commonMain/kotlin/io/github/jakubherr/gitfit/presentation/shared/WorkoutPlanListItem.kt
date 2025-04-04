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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan

@Composable
fun WorkoutPlanListItem(
    workout: WorkoutPlan,
    modifier: Modifier = Modifier,
    onItemClicked: () -> Unit = {},
    actionSlot: @Composable () -> Unit = { Icon(Icons.Default.Delete, "") },
    onActionClicked: () -> Unit = {},
) {
    Card(
        onItemClicked,
        modifier
    ) {
        Column(
            Modifier.padding(8.dp)
        ) {
            Row(
                modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(workout.name, fontWeight = FontWeight.Bold)

                IconButton(onActionClicked) {
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
    modifier: Modifier = Modifier
) {
    Text(
        if (blockList.isNotEmpty()) blockList.joinToString { it.exercise.name } else "Click to add exercise",
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
    )
}