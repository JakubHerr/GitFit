package io.github.jakubherr.gitfit.presentation.graph

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.autoScaleYRange
import io.github.koalaplot.core.xygraph.rememberIntLinearAxisModel

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun BasicLineGraph(
    data: List<DefaultPoint<String, Int>>,
    modifier: Modifier = Modifier,
    title: String = "",
) {
    ChartLayout(
        modifier.padding(16.dp),
        title = { Text(title) }
    ) {
        val dates = data.map { it.x }
        XYGraph(
            CategoryAxisModel(dates),
            rememberIntLinearAxisModel(data.autoScaleYRange()),
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
