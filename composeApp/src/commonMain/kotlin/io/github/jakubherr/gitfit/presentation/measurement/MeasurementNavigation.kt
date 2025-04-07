package io.github.jakubherr.gitfit.presentation.measurement

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.github.jakubherr.gitfit.presentation.MeasurementAddEditRoute
import io.github.jakubherr.gitfit.presentation.MeasurementRoute
import io.github.jakubherr.gitfit.presentation.workout.sharedViewModel
import kotlinx.coroutines.launch


fun NavGraphBuilder.measurementGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    composable<MeasurementRoute> {
        val vm = navController.sharedViewModel<MeasurementViewModel>()

        MeasurementScreenRoot(
            vm = vm,
            onRequestAddEditMeasurement = { navController.navigate(MeasurementAddEditRoute) }
        )
    }

    composable<MeasurementAddEditRoute> {
        val vm = navController.sharedViewModel<MeasurementViewModel>()
        val today by vm.todayMeasurement.collectAsStateWithLifecycle()
        val scope = rememberCoroutineScope()

        AddEditMeasurement(
            oldMeasurement = today,
            onSave = { measurement ->
                if (!measurement.isValid) scope.launch { snackbarHostState.showSnackbar("All measurement values must be positive") }
                else {
                    vm.onAction(MeasurementAction.SaveMeasurement(measurement))
                    navController.popBackStack()
                }
            }
        )
    }
}