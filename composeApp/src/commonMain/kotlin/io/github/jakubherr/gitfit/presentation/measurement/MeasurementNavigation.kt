package io.github.jakubherr.gitfit.presentation.measurement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.error_invalid_measurement_values
import io.github.jakubherr.gitfit.presentation.MeasurementAddEditRoute
import io.github.jakubherr.gitfit.presentation.MeasurementHistoryRoute
import io.github.jakubherr.gitfit.presentation.MeasurementRoute
import io.github.jakubherr.gitfit.presentation.workout.sharedViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

fun NavGraphBuilder.measurementGraph(
    navController: NavHostController,
    showSnackbar: (String) -> Unit,
) {
    composable<MeasurementRoute> {
        val vm = navController.sharedViewModel<MeasurementViewModel>()

        MeasurementScreenRoot(
            vm = vm,
            onRequestAddEditMeasurement = { navController.navigate(MeasurementAddEditRoute) },
            onShowHistory = { navController.navigate(MeasurementHistoryRoute) },
        )
    }

    composable<MeasurementAddEditRoute> {
        val vm = navController.sharedViewModel<MeasurementViewModel>()
        val today by vm.todayMeasurement.collectAsStateWithLifecycle()
        val scope = rememberCoroutineScope()

        AddEditMeasurement(
            oldMeasurement = today,
            onSave = { measurement ->
                if (!measurement.isValid) {
                    scope.launch { showSnackbar(getString(Res.string.error_invalid_measurement_values)) }
                } else {
                    vm.onAction(MeasurementAction.SaveMeasurement(measurement))
                    navController.popBackStack()
                }
            },
        )
    }

    composable<MeasurementHistoryRoute> {
        val vm = navController.sharedViewModel<MeasurementViewModel>()
        val measurements by vm.allUserMeasurements.collectAsStateWithLifecycle(emptyList())

        MeasurementHistoryScreen(
            measurements,
            onDeleteMeasurement = {
                vm.onAction(MeasurementAction.DeleteMeasurement(it))
            },
        )
    }
}
