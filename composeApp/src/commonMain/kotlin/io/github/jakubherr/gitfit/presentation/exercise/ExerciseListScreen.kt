package io.github.jakubherr.gitfit.presentation.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.create_exercise
import gitfit.composeapp.generated.resources.error_exercise_list_empty
import gitfit.composeapp.generated.resources.search_exercise
import io.github.jakubherr.gitfit.domain.model.Exercise
import org.jetbrains.compose.resources.stringResource

// Use case: show a list of existing exercises
// the purpose is to either browse and show detail or add exercise to workout (real time or planned)
@Composable
fun ExerciseListScreenRoot(
    vm: ExerciseViewModel,
    modifier: Modifier = Modifier,
    onExerciseClick: (Exercise) -> Unit = {},
    onCreateExerciseClick: () -> Unit = {},
) {
    val default by vm.defaultExercises.collectAsStateWithLifecycle(emptyList())
    val custom by vm.customExercises.collectAsStateWithLifecycle(emptyList())

    ExerciseListScreen(
        exerciseList = (default + custom).sortedBy { it.name },
        onExerciseClick = { onExerciseClick(it) },
        onAddExerciseClick = { onCreateExerciseClick() },
    )
}

@Composable
fun ExerciseListScreen(
    exerciseList: List<Exercise>,
    modifier: Modifier = Modifier,
    onExerciseClick: (Exercise) -> Unit = {},
    onAddExerciseClick: () -> Unit = {},
) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var query by remember { mutableStateOf("") }
        SearchBar(
            query,
            onQueryChange = { query = it },
            Modifier.padding(16.dp),
        )

        Spacer(Modifier.height(32.dp))

        if (exerciseList.isEmpty()) {
            Column(
                Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(Icons.Default.Block, null, Modifier.size(128.dp).alpha(0.6f))
                Text(stringResource(Res.string.error_exercise_list_empty))
            }
        } else {
            val filteredList =
                if (query.isNotBlank()) exerciseList.filter { it.name.startsWith(query, ignoreCase = true) } else exerciseList

            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(filteredList) { exercise ->
                    ExerciseListItem(exercise) { onExerciseClick(exercise) }
                    HorizontalDivider(Modifier.height(1.dp))
                }
            }
        }
        Row {
            Button(onAddExerciseClick) {
                Text(stringResource(Res.string.create_exercise))
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        query,
        onQueryChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Search, null) },
        placeholder = { Text(stringResource(Res.string.search_exercise)) },
    )
}
