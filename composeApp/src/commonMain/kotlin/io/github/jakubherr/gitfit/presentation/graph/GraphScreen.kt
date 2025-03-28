package io.github.jakubherr.gitfit.presentation.graph

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.autoScaleXRange
import io.github.koalaplot.core.xygraph.autoScaleYRange
import io.github.koalaplot.core.xygraph.rememberDoubleLinearAxisModel
import io.github.koalaplot.core.xygraph.rememberIntLinearAxisModel

// use case: browse previous records, measurements over a month/year
@Composable
fun GraphScreenRoot(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize()
    ) {
        val data = buildList {
            add(DefaultPoint(1, 80.0))
            add(DefaultPoint(2, 70.25))
            // add(DefaultPoint(3, 60))
            // add(DefaultPoint(4, 60))
            add(DefaultPoint(5, 65.30))
            add(DefaultPoint(6, 70.1))
            add(DefaultPoint(7, 75.0))
        }

        SimpleGraph(Modifier.padding(16.dp).fillMaxWidth().height(256.dp), data)
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun SimpleGraph(
    modifier: Modifier = Modifier,
    data: List<DefaultPoint<Int, Double>>,
) {

    Row(modifier) {
        XYGraph(
            rememberIntLinearAxisModel(data.autoScaleXRange()),
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
