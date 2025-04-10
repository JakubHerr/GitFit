package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.jakubherr.gitfit.domain.model.Workout

@Composable
fun WorkoutListItem(
    workout: Workout,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
) {
    Card(
        onClick = { onClick(workout.id) },
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
                Text(workout.date.toString(), fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(4.dp))

            Row {
                ExerciseNames(workout.blocks)
            }
        }
    }
}
