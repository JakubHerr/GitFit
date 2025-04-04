package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.enums.EnumEntries

@Composable
fun <T: Enum<T>> MultipleChoiceChipSelection(
    choices: EnumEntries<T>,
    selected: List<T>,
    modifier: Modifier = Modifier,
    onChoiceSelected: (T) -> Unit = {},
) {
    LazyRow(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(choices) { choice ->
            FilterChip(
                selected = selected.contains(choice),
                onClick = { onChoiceSelected(choice) },
                label = { Text(choice.name) }
            )
        }
    }
}