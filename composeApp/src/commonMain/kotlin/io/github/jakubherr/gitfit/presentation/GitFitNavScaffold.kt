package io.github.jakubherr.gitfit.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SettingsAccessibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.dashboard
import gitfit.composeapp.generated.resources.history
import gitfit.composeapp.generated.resources.measurement
import gitfit.composeapp.generated.resources.plan
import gitfit.composeapp.generated.resources.profile
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val label: StringResource,
    val icon: ImageVector,
    val route: KClass<*>,
) {
    DASHBOARD(Res.string.dashboard, Icons.Default.Home, DashboardRoute::class),
    PLAN(Res.string.plan, Icons.Default.EditCalendar, PlanOverviewRoute::class),
    MEASUREMENT(Res.string.measurement, Icons.Default.SettingsAccessibility, MeasurementRoute::class),
    HISTORY(Res.string.history, Icons.AutoMirrored.Filled.ManageSearch, HistoryRoute::class),
    PROFILE(Res.string.profile, Icons.Default.AccountBox, SettingsRoute::class),
}

@Composable
fun GitFitNavScaffold(
    modifier: Modifier = Modifier,
    currentDestination: TopLevelDestination?,
    showDestinations: Boolean = currentDestination != null,
    onDestinationClicked: (TopLevelDestination) -> Unit = {},
    content: @Composable () -> Unit,
) {
    val layoutType =
        if (showDestinations) {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
                currentWindowAdaptiveInfo(),
            )
        } else {
            NavigationSuiteType.None
        }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            null,
                        )
                    },
                    label = { Text(stringResource(it.label)) },
                    selected = it == currentDestination,
                    onClick = { onDestinationClicked(it) },
                )
            }
        },
        layoutType = layoutType,
        modifier = modifier,
        content = content,
    )
}
