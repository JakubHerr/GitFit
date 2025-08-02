package io.github.jakubherr.gitfit.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import io.github.jakubherr.gitfit.presentation.auth.AuthViewModel
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardAction
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardScreenRoot
import io.github.jakubherr.gitfit.presentation.exercise.exerciseNavigation
import io.github.jakubherr.gitfit.presentation.graph.HistoryScreen
import io.github.jakubherr.gitfit.presentation.measurement.measurementNavigation
import io.github.jakubherr.gitfit.presentation.planning.PlanningViewModel
import io.github.jakubherr.gitfit.presentation.planning.planningNavigation
import io.github.jakubherr.gitfit.presentation.settings.SettingsScreenRoot
import io.github.jakubherr.gitfit.presentation.workout.WorkoutViewModel
import io.github.jakubherr.gitfit.presentation.workout.workoutNavigation
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.core.annotation.KoinExperimentalAPI

// This nested navigation graph contains all destinations that require a logged in user
fun NavGraphBuilder.loggedInNavigation(
    navController: NavHostController,
    showSnackbar: (String) -> Unit,
    authViewModel: AuthViewModel,
) {
    navigation<LoggedInRoute>(
        startDestination = DashboardRoute,
    ) {
        composable<DashboardRoute> {
            val vm = navController.sharedViewModel<WorkoutViewModel>()
            val planVm = navController.sharedViewModel<PlanningViewModel>()

            DashboardScreenRoot(
                vm = vm,
                planVM = planVm,
                onAction = { action ->
                    when (action) {
                        is DashboardAction.PlannedWorkoutClick,
                        DashboardAction.UnplannedWorkoutClick,
                        DashboardAction.ResumeWorkoutClick,
                        -> {
                            navController.navigate(WorkoutInProgressRoute)
                        }
                    }
                },
                onPlanSelected = {
                    navController.navigate(PlanDetailRoute(it.id))
                },
            )
        }

        workoutNavigation(navController, showSnackbar = showSnackbar)

        planningNavigation(navController, showSnackbar = showSnackbar)

        measurementNavigation(navController, showSnackbar = showSnackbar)

        exerciseNavigation(navController)

        composable<HistoryRoute> {
            HistoryScreen(
                onBrowseWorkoutData = {
                    navController.navigate(WorkoutHistoryRoute)
                },
                onBrowseExerciseData = { navController.navigate(ExerciseListRoute) },
                onBrowseMeasurementData = { navController.navigate(MeasurementHistoryRoute) },
            )
        }

        composable<SettingsRoute> {
            SettingsScreenRoot(authViewModel)
        }
    }
}

// This function makes sure the fetched viewmodel is scoped to LoggedInRoute
// The same viewmodel instance is reused in loggedInGraph
// basically, when the user logs out, all viewModels that may be holding his data are destroyed
@OptIn(KoinExperimentalAPI::class)
@Composable
inline fun <reified T : ViewModel> NavHostController.sharedViewModel(): T =
    koinNavViewModel<T>(viewModelStoreOwner = getBackStackEntry(LoggedInRoute))
