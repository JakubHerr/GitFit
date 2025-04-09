package io.github.jakubherr.gitfit.presentation.workout

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import io.github.jakubherr.gitfit.presentation.DashboardRoute
import io.github.jakubherr.gitfit.presentation.ExerciseListRoute
import io.github.jakubherr.gitfit.presentation.HistoryRoute
import io.github.jakubherr.gitfit.presentation.LoggedInRoute
import io.github.jakubherr.gitfit.presentation.MeasurementHistoryRoute
import io.github.jakubherr.gitfit.presentation.PlanDetailRoute
import io.github.jakubherr.gitfit.presentation.SettingsRoute
import io.github.jakubherr.gitfit.presentation.WorkoutHistoryRoute
import io.github.jakubherr.gitfit.presentation.WorkoutInProgressRoute
import io.github.jakubherr.gitfit.presentation.auth.AuthViewModel
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardAction
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardScreenRoot
import io.github.jakubherr.gitfit.presentation.exercise.exerciseNavigation
import io.github.jakubherr.gitfit.presentation.graph.HistoryScreenRoot
import io.github.jakubherr.gitfit.presentation.measurement.measurementNavigation
import io.github.jakubherr.gitfit.presentation.planning.PlanningViewModel
import io.github.jakubherr.gitfit.presentation.planning.planningNavigation
import io.github.jakubherr.gitfit.presentation.settings.SettingsScreenRoot
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.core.annotation.KoinExperimentalAPI

// This nested navigation graph contains all destinations that require a logged in user
fun NavGraphBuilder.loggedInGraph(
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
            HistoryScreenRoot(
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
inline fun <reified T : ViewModel> NavHostController.sharedViewModel(): T {
    return koinNavViewModel<T>(viewModelStoreOwner = getBackStackEntry(LoggedInRoute))
}
