package io.github.jakubherr.gitfit.previews

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.presentation.measurement.AddEditMeasurementScreen
import io.github.jakubherr.gitfit.presentation.measurement.MeasurementHistoryScreen
import io.github.jakubherr.gitfit.ui.theme.GitFitTheme

@Preview
@Composable
private fun MeasurementHistoryPreview() {
    GitFitTheme {
        Surface {
            MeasurementHistoryScreen(
                listOf(mockMeasurement, mockMeasurement, mockMeasurement)
            )
        }
    }
}

@Preview
@Composable
private fun AddEditMeasurementScreenPreview() {
    GitFitTheme {
        Surface {
            AddEditMeasurementScreen(
                oldMeasurement = mockMeasurement
            )
        }
    }
}
