package io.github.jakubherr.gitfit.presentation.graph

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
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
import org.jetbrains.compose.resources.stringResource

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
    val data = measurementList
        .map {
            val value = when (measurementType) {
                MeasurementType.NECK -> it.neck
                MeasurementType.CHEST -> it.chest
                MeasurementType.LEFT_ARM -> it.leftArm
                MeasurementType.RIGHT_ARM -> it.rightArm
                MeasurementType.LEFT_FOREARM -> it.leftForearm
                MeasurementType.RIGHT_FOREARM -> it.rightForearm
                MeasurementType.WAIST -> it.waist
                MeasurementType.LEFT_THIGH -> it.leftThigh
                MeasurementType.RIGHT_THIGH -> it.rightThigh
                MeasurementType.LEFT_CALF -> it.leftCalf
                MeasurementType.RIGHT_CALF -> it.rightCalf
                MeasurementType.WEIGHT -> it.bodyweight
                MeasurementType.HEIGHT -> it.height
            }
            it.date to value
        }.filter { it.second != null }
        .map { DefaultPoint(it.first.toString(), it.second!!) }

    val title = if (data.isNotEmpty()) "last measurement: ${data.last().y} ${measurementType.unit}" else "No data"

    ChartLayout(
        modifier.padding(16.dp),
        title = { Text(title) }
    ) {
        XYGraph(
            CategoryAxisModel(data.map { it.x }),
            rememberDoubleLinearAxisModel(data.autoScaleYRange()),
        ) {
            LinePlot(
                data,
                lineStyle = LineStyle(SolidColor(Color.Blue), 2.dp),
                symbol = {
                    Symbol(
                        shape = RoundedCornerShape(8.dp),
                        fillBrush = SolidColor(Color.Blue)
                    )
                }
            )
        }
    }
}