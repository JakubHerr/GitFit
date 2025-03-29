package io.github.jakubherr.gitfit.presentation.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import io.github.koalaplot.core.xygraph.autoScaleXRange
import io.github.koalaplot.core.xygraph.autoScaleYRange
import io.github.koalaplot.core.xygraph.rememberDoubleLinearAxisModel
import io.github.koalaplot.core.xygraph.rememberIntLinearAxisModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.enums.EnumEntries

// use case: browse previous records, measurements over a month/year
@Composable
fun GraphScreenRoot(
    vm: GraphViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxSize()
    ) {
        // TODO: graphs that would be in exercise detail
        //  time scale selection
        //  chip selection with type of measurement:



        // TODO: graphs that visualize user body measurements

        val data = buildList {
            add(DefaultPoint(1, 80.0))
            add(DefaultPoint(2, 70.25))
            // add(DefaultPoint(3, 60))
            // add(DefaultPoint(4, 60))
            add(DefaultPoint(5, 65.30))
            add(DefaultPoint(6, 70.1))
            add(DefaultPoint(7, 75.0))
        }

        val data2 = buildList {
            add(DefaultPoint("Mon", 80.0))
            add(DefaultPoint("Tue", 70.25))
            add(DefaultPoint("Wed", 60.0))
            add(DefaultPoint("Thu", 60.0))
            add(DefaultPoint("Fri", 65.30))
            //add(DefaultPoint("Sat", 70.1))
            add(DefaultPoint("Sun", 75.0))
        }

        SimpleGraph(Modifier.fillMaxWidth().height(256.dp), data)
        MonthlyGraph(Modifier.fillMaxWidth().height(256.dp), data2)

        var selected by remember { mutableStateOf(ExerciseMetric.HEAVIEST_WEIGHT) }

        SingleChoiceChipSelection(
            ExerciseMetric.entries,
            selected = selected,
            modifier = Modifier.padding(16.dp),
            onChoiceSelected = {
                println("DBG: ${it.name} selected")
                selected = it
            }
        )
    }
}

enum class ExerciseMetric{
    HEAVIEST_WEIGHT,
    BEST_SET_VOLUME,
    TOTAL_WORKOUT_VOLUME,
    TOTAL_REPETITIONS
}

@Composable
fun <T: Enum<T>> SingleChoiceChipSelection(
    choices: EnumEntries<T>,
    selected: T,
    modifier: Modifier = Modifier,
    onChoiceSelected: (T) -> Unit = {},
) {
    LazyRow(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(choices) { choice ->
            FilterChip(
                selected = choice == selected,
                onClick = { onChoiceSelected(choice) },
                label = { Text(choice.name) }
            )
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun MonthlyGraph(
    modifier: Modifier = Modifier,
    data: List<DefaultPoint<String, Double>>,
) {
    ChartLayout(
        modifier.padding(16.dp),
        title = { Text("Some txt") }
    ) {
        XYGraph(
            CategoryAxisModel(listOf("Mon","Tue","Wed", "Thu", "Fri","Sat","Sun"), true),
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

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun SimpleGraph(
    modifier: Modifier = Modifier,
    data: List<DefaultPoint<Int, Double>>,
) {
    ChartLayout(
        modifier.padding(16.dp),
        title = { Text("Some txt") }
    ) {
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
