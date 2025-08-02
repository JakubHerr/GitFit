package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.create_new_plan
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.presentation.shared.PlanSectionLazyColumn
import org.jetbrains.compose.resources.stringResource

// use case: plan workouts to be performed in the future (weeks, months)
@Composable
fun PlanListScreenRoot(
    vm: PlanningViewModel,
    modifier: Modifier = Modifier,
    onCreateNewPlan: () -> Unit = {},
    onUserPlanSelected: (Plan) -> Unit = {},
    onDefaultPlanSelected: (Plan) -> Unit = { },
) {
    val userPlans by vm.userPlans.collectAsStateWithLifecycle(emptyList())
    val predefinedPlans by vm.predefinedPlans.collectAsStateWithLifecycle(emptyList())

    PlanListScreen(
        userPlans,
        predefinedPlans,
        modifier,
        onCreateNewPlan,
        onUserPlanSelected,
        onDefaultPlanSelected,
    )
}

@Composable
fun PlanListScreen(
    userPlans: List<Plan>,
    predefinedPlans: List<Plan>,
    modifier: Modifier = Modifier,
    onCreateNewPlan: () -> Unit = {},
    onUserPlanSelected: (Plan) -> Unit = { },
    onDefaultPlanSelected: (Plan) -> Unit = { },
) {
    Surface {
        Column(
            modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Top,
        ) {
            Column(
                Modifier.weight(1.0f),
            ) {
                PlanSectionLazyColumn(
                    userPlans,
                    predefinedPlans,
                    onUserPlanSelected,
                    onDefaultPlanSelected,
                )
            }

            Column(
                Modifier.fillMaxWidth().weight(0.1f, fill = true),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Button(
                    onClick = onCreateNewPlan,
                    modifier = Modifier.testTag("CreateNewPlanButton"),
                ) {
                    Text(stringResource(Res.string.create_new_plan))
                }
            }
        }
    }
}
