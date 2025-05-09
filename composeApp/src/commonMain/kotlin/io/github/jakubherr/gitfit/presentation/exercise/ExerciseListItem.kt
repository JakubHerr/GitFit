package io.github.jakubherr.gitfit.presentation.exercise

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.presentation.shared.translation

@Composable
fun ExerciseListItem(
    exercise: Exercise,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit,
) {
    Row(
        modifier.fillMaxWidth().sizeIn(minHeight = 48.dp).clickable { onItemClick(exercise.id) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(exercise.name)
            Text(exercise.primaryMuscle.translation())
        }
        Icon(Icons.Default.ChevronRight, null, modifier.size(32.dp))
    }
}
