package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import io.github.jakubherr.gitfit.presentation.shared.WorkoutPlanSection
import org.jetbrains.compose.resources.stringResource

// use case: plan workouts to be performed in the future (weeks, months)
@Composable
fun PlanListScreenRoot(
    vm: PlanningViewModel,
    modifier: Modifier = Modifier,
    onCreateNewPlan: () -> Unit = {},
    onUserPlanSelected: (Plan) -> Unit = {},
) {
    val userPlans by vm.userPlans.collectAsStateWithLifecycle(emptyList())
    val predefinedPlans by vm.predefinedPlans.collectAsStateWithLifecycle(emptyList())

    PlanListScreen(
        userPlans,
        predefinedPlans,
        modifier,
        onCreateNewPlan,
        onUserPlanSelected,
    )
}

@Composable
fun PlanListScreen(
    userPlans: List<Plan>,
    predefinedPlans: List<Plan>,
    modifier: Modifier = Modifier,
    onCreateNewPlan: () -> Unit = {},
    onUserPlanSelected: (Plan) -> Unit = { },
) {
    Surface {
        Column(
            modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Top,
        ) {
            Column(
                Modifier.weight(1.0f)
            ) {
                WorkoutPlanSection(
                    userPlans,
                    stringResource(Res.string.your_plans),
                    modifier = Modifier.weight(1.0f, fill = false),
                    onPlanSelected = onUserPlanSelected
                )

                WorkoutPlanSection(
                    predefinedPlans,
                    stringResource(Res.string.predefined_plans),
                    modifier = Modifier.weight(1.0f, fill = false),
                    onPlanSelected = { /* TODO */ }
                )
            }

            Column(
                Modifier.fillMaxWidth().weight(0.1f, fill = true),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Button(onCreateNewPlan) {
                    Text(stringResource(Res.string.create_new_plan))
                }
            }
        }
    }
}
