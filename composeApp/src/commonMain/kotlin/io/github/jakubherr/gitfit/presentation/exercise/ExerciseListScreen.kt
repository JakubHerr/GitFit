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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.create_exercise
import gitfit.composeapp.generated.resources.error_no_exercise_found
import gitfit.composeapp.generated.resources.search_exercise
import io.github.jakubherr.gitfit.domain.model.Exercise
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Use case: show a list of existing exercises
// the purpose is to either browse and show detail or add exercise to workout (real time or planned)
@Composable
fun ExerciseListScreenRoot(
    vm: ExerciseViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onExerciseClick: (Exercise) -> Unit = {},
    onCreateExerciseClick: () -> Unit = {},
) {
    val default by vm.defaultExercises.collectAsStateWithLifecycle(emptyList())
    val custom by vm.customExercises.collectAsStateWithLifecycle(emptyList())

    ExerciseListScreen(
        exerciseList = default + custom,
    ) { action ->
        if (action is ExerciseAction.CreateExerciseSelected) onCreateExerciseClick()
        if (action is ExerciseAction.ExerciseSelected) onExerciseClick(action.exercise)
    }
}

@Composable
fun ExerciseListScreen(
    exerciseList: List<Exercise>,
    modifier: Modifier = Modifier,
    onAction: (ExerciseAction) -> Unit = {},
) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // TODO implement basic search and filtering based on category
        SearchBar(
            "",
            onQueryChange = {},
            Modifier.padding(16.dp),
        )
        Spacer(Modifier.height(32.dp))

        if (exerciseList.isEmpty()) {
            Column(
                Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(Icons.Default.Block, "", Modifier.size(128.dp).alpha(0.6f))
                Text(stringResource(Res.string.error_no_exercise_found))
            }
        } else {
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(exerciseList) { exercise ->
                    ExerciseListItem(exercise) { onAction(ExerciseAction.ExerciseSelected(exercise)) }
                    HorizontalDivider(Modifier.height(1.dp))
                }
            }
        }
        Row {
            Button(onClick = { onAction(ExerciseAction.CreateExerciseSelected) }) {
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
        leadingIcon = { Icon(Icons.Default.Search, "") },
        placeholder = { Text(stringResource(Res.string.search_exercise)) },
    )
}
