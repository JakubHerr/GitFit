package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import org.koin.compose.viewmodel.koinViewModel

// use case: plan workouts to be performed in the future (weeks, months)
@Composable
fun PlanOverviewScreenRoot(
    vm: PlanningViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onCreateNewPlan: () -> Unit = {},
) {
    val userWorkouts by vm.userWorkouts.collectAsStateWithLifecycle(emptyList())

    Column(modifier.fillMaxSize()) {
        Text("Your plans")
        LazyColumn {
            items(userWorkouts) { wo ->
                // TODO UI card for workout
                Text(wo.blocks.joinToString())
            }
        }

        Text("Predefined plans")
        LazyColumn {
            items(3) { idx -> Text("Predefined plan #$idx") }
        }

        Button(onCreateNewPlan) {
            Text("Create a new plan")
        }
    }
}

@Composable
fun PlanListItem(
    workout: WorkoutPlan,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(onClick) {
        Column {
            Text(workout.name)
            Text(workout.blocks.joinToString())
        }
    }
}
