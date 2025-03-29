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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import org.koin.compose.viewmodel.koinViewModel
import kotlin.enums.EnumEntries

// use case: browse previous records, measurements over a month/year
@Composable
fun GraphScreenRoot(
    vm: GraphViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val values by vm.dataPoints.collectAsStateWithLifecycle()

    Column(
        modifier.fillMaxSize()
    ) {
        // TODO: graphs that would be in exercise detail
        //  time scale selection

        // TODO: graphs that visualize user body measurements

        LastTenGraph(Modifier.fillMaxWidth().height(256.dp), values)

        // TODO header that shows the highest value

        SingleChoiceChipSelection(
            ExerciseMetric.entries,
            selected = vm.selectedMetric,
            modifier = Modifier.padding(16.dp),
            onChoiceSelected = {
                println("DBG: ${it.name} selected")
                vm.onAction(GraphAction.ExerciseMetricSelected(it))
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
fun LastTenGraph(
    modifier: Modifier = Modifier,
    data: List<DefaultPoint<String, Int>>,
) {
    ChartLayout(
        modifier.padding(16.dp),
        title = { Text("Last 10 workouts - ") }
    ) {
        val dates = data.map { it.x }
        XYGraph(
            CategoryAxisModel(dates),
            rememberIntLinearAxisModel(data.autoScaleYRange()),
            // zoomEnabled = true,
            // panEnabled = true
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
