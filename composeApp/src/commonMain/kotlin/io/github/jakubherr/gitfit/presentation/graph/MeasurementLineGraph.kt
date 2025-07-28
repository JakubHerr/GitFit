package io.github.jakubherr.gitfit.presentation.graph

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.latest_measurements
import gitfit.composeapp.generated.resources.no_data
import io.github.jakubherr.gitfit.domain.model.Measurement
import io.github.jakubherr.gitfit.presentation.measurement.MeasurementType
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.autoScaleYRange
import io.github.koalaplot.core.xygraph.rememberDoubleLinearAxisModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringResource
import kotlin.let

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun MeasurementLineGraph(
    measurementList: List<Measurement>,
    measurementType: MeasurementType,
    modifier: Modifier = Modifier,
) {
    // take list of measurements
    // filter out measurements that are missing the selected type
    // map these measurements to a DefaultPoint of date and value
    val data: List<DefaultPoint<String, Double>> = measurementList.mapNotNull { measurement ->
        val value = when (measurementType) {
            MeasurementType.NECK -> measurement.neck
            MeasurementType.CHEST -> measurement.chest
            MeasurementType.LEFT_ARM -> measurement.leftArm
            MeasurementType.RIGHT_ARM -> measurement.rightArm
            MeasurementType.LEFT_FOREARM -> measurement.leftForearm
            MeasurementType.RIGHT_FOREARM -> measurement.rightForearm
            MeasurementType.WAIST -> measurement.waist
            MeasurementType.LEFT_THIGH -> measurement.leftThigh
            MeasurementType.RIGHT_THIGH -> measurement.rightThigh
            MeasurementType.LEFT_CALF -> measurement.leftCalf
            MeasurementType.RIGHT_CALF -> measurement.rightCalf
            MeasurementType.WEIGHT -> measurement.bodyweight
            MeasurementType.HEIGHT -> measurement.height
        }
        value?.let { DefaultPoint("${measurement.date.day}.${measurement.date.month.number}.", it) }
    }

    val title =
        if (data.isNotEmpty()) {
            "${stringResource(Res.string.latest_measurements)} ${data.last().y} ${measurementType.unit}"
        } else {
            stringResource(Res.string.no_data)
        }

    ChartLayout(
        modifier.padding(16.dp),
        title = { Text(title) },
    ) {
        XYGraph(
            CategoryAxisModel(data.map { it.x }),
            rememberDoubleLinearAxisModel(data.autoScaleYRange()),
        ) {
            LinePlot(
                data,
                lineStyle = LineStyle(SolidColor(MaterialTheme.colorScheme.primary), 2.dp),
                symbol = {
                    Symbol(
                        shape = RoundedCornerShape(8.dp),
                        fillBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    )
                },
            )
        }
    }
}
