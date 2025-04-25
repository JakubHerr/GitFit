package io.github.jakubherr.gitfit.previews

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.domain.model.User
import io.github.jakubherr.gitfit.presentation.auth.AuthState
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardScreen
import io.github.jakubherr.gitfit.presentation.graph.HistoryScreen
import io.github.jakubherr.gitfit.presentation.measurement.MeasurementScreen
import io.github.jakubherr.gitfit.presentation.planning.PlanListScreen
import io.github.jakubherr.gitfit.presentation.settings.SettingsScreen
import io.github.jakubherr.gitfit.ui.theme.GitFitTheme

// hint: use preview settings to check out different devices
// device = "spec:width=1920dp,height=1080dp,dpi=160" for desktop
// locale = "cs" for translation
@Preview
@Composable
private fun DashboardScreenPreview() {
    GitFitTheme {
        Surface {
            DashboardScreen(
                userPlans = listOf(mockPlan, mockPlan)
            )
        }
    }
}

@Composable
@Preview
fun PlanListScreenPreview(modifier: Modifier = Modifier) {
    GitFitTheme {
        Surface {
            PlanListScreen(
                userPlans = listOf(mockPlan),
                predefinedPlans = listOf(mockPlan, mockPlan, mockPlan, mockPlan)
            )
        }
    }
}

@Preview
@Composable
private fun MeasurementScreenPreview() {
    GitFitTheme {
        Surface {
            MeasurementScreen(
                listOf(),
                null,
            )
        }
    }
}

@Preview
@Composable
private fun HistoryScreenPreview() {
    GitFitTheme {
        Surface {
            HistoryScreen {

            }
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    GitFitTheme {
        Surface {
            SettingsScreen(
                authState = AuthState(User.LoggedOut, loading = true, error = null),
                onLogout = { },
                onChangePassword = { _,_ -> },
                onDeleteAccount = { }
            )
        }
    }
}
