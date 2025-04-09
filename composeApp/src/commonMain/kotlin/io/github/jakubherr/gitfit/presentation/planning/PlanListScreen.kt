package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.create_new_plan
import gitfit.composeapp.generated.resources.predefined_plans
import gitfit.composeapp.generated.resources.your_plans
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.presentation.shared.ExerciseNames
import org.jetbrains.compose.resources.stringResource

// use case: plan workouts to be performed in the future (weeks, months)
@Composable
fun PlanListScreenRoot(
    vm: PlanningViewModel,
    modifier: Modifier = Modifier,
    onCreateNewPlan: () -> Unit = {},
    onPlanSelected: (Plan) -> Unit = {},
) {
    val userPlans by vm.userPlans.collectAsStateWithLifecycle(emptyList())
    val predefinedPlans by vm.predefinedPlans.collectAsStateWithLifecycle(emptyList())

    PlanListScreen(
        userPlans,
        predefinedPlans,
        modifier,
        onCreateNewPlan,
        onPlanSelected,
    )
}

@Composable
fun PlanListScreen(
    userPlans: List<Plan>,
    predefinedPlans: List<Plan>,
    modifier: Modifier = Modifier,
    onCreateNewPlan: () -> Unit = {},
    onPlanSelected: (Plan) -> Unit = { },
) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(
            modifier = Modifier.weight(1.0f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Text(stringResource(Res.string.your_plans)) }

            items(userPlans) { plan ->
                PlanListItem(
                    plan,
                    modifier = Modifier.padding(8.dp),
                ) {
                    onPlanSelected(plan)
                }
            }

            item { Text(stringResource(Res.string.predefined_plans)) }
            items(predefinedPlans) { plan ->
                PlanListItem(plan) {
                    // TODO predefined plans
                }
            }
        }

        Button(onCreateNewPlan) {
            Text(stringResource(Res.string.create_new_plan))
        }
    }
}

@Composable
fun PlanListItem(
    plan: Plan,
    modifier: Modifier = Modifier,
    onPlanClicked: (Plan) -> Unit = {},
) {
    // name of plan
    Card({ onPlanClicked(plan) }) {
        Column(modifier.fillMaxWidth().padding(8.dp)) {
            Text(plan.name, fontWeight = FontWeight.Bold)
            plan.workoutPlans.forEach { workout ->
                Text(workout.name, fontWeight = FontWeight.SemiBold)
                ExerciseNames(workout.blocks)
            }
        }
    }
}
