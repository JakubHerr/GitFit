package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.list_is_empty
import io.github.jakubherr.gitfit.domain.model.Plan
import org.jetbrains.compose.resources.stringResource

@Composable
fun WorkoutPlanSection(
    workoutsPlans: List<Plan>,
    title: String,
    modifier: Modifier = Modifier,
    onPlanSelected: (Plan) -> Unit,
) {
    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth().sizeIn(minHeight = 48.dp).clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title)

            val icon = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
            Icon(icon, "")
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.animateContentSize(),
            verticalArrangement = if (expanded) Arrangement.spacedBy(16.dp) else Arrangement.spacedBy(0.dp),
        ) {
            items(workoutsPlans) { plan ->
                if (expanded) PlanListItem(plan) { onPlanSelected(it) }
            }
            item {
                if (expanded && workoutsPlans.isEmpty()) Text(stringResource(Res.string.list_is_empty), fontWeight = FontWeight.Light)
            }
        }
    }
}

@Composable
private fun PlanListItem(
    plan: Plan,
    modifier: Modifier = Modifier,
    onPlanClicked: (Plan) -> Unit = {},
) {
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
