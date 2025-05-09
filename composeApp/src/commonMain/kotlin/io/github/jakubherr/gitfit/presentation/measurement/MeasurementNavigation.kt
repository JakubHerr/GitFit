package io.github.jakubherr.gitfit.presentation.measurement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.empty_measurement_history
import gitfit.composeapp.generated.resources.error_invalid_measurement_values
import io.github.jakubherr.gitfit.presentation.MeasurementAddEditRoute
import io.github.jakubherr.gitfit.presentation.MeasurementHistoryRoute
import io.github.jakubherr.gitfit.presentation.MeasurementRoute
import io.github.jakubherr.gitfit.presentation.sharedViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

fun NavGraphBuilder.measurementNavigation(
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

        AddEditMeasurementScreen(
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

        if (measurements.isEmpty()) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(stringResource(Res.string.empty_measurement_history))
            }
        } else {
            MeasurementHistoryScreen(
                measurements.sortedByDescending { it.date },
                onDeleteMeasurement = {
                    vm.onAction(MeasurementAction.DeleteMeasurement(it))
                },
            )
        }
    }
}
