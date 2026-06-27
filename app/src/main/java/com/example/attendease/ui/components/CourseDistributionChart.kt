package com.example.attendease.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.attendease.dto.response.PiePointResponse
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.entryModelOf

@Composable
fun CourseDistributionChart(
    distribution: List<PiePointResponse>,
    modifier: Modifier = Modifier
) {
    if (distribution.isEmpty()) return

    val labels = distribution.map { it.label }
    val counts = distribution.map { it.count.toFloat() }

    // Create entry models
    val chartEntryModel = entryModelOf(*counts.toTypedArray())

    // Formatter for bottom axis (course labels)
    val horizontalAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        val index = value.toInt()
        if (index >= 0 && index < labels.size) labels[index] else ""
    }

    // Bind to your custom theme colors
    val columnColor = MaterialTheme.colorScheme.primary
    val axisLabelColor = MaterialTheme.colorScheme.onSurface
    val axisLineColor = MaterialTheme.colorScheme.outlineVariant

    // Create dynamic components for Vico
    val labelComponent = textComponent(color = axisLabelColor)
    val axisLineComponent = lineComponent(color = axisLineColor, thickness = 1.dp)
    val guidelineComponent = lineComponent(color = axisLineColor.copy(alpha = 0.5f), thickness = 1.dp)

    Chart(
        chart = columnChart(
            columns = listOf(
                lineComponent(
                    color = columnColor, 
                    thickness = 32.dp, 
                    shape = Shapes.roundedCornerShape(topLeftPercent = 50, topRightPercent = 50)
                )
            )
        ),
        model = chartEntryModel,
        startAxis = rememberStartAxis(
            label = labelComponent,
            axis = axisLineComponent,
            guideline = guidelineComponent,
            tick = axisLineComponent,
            valueFormatter = { value, _ -> value.toInt().toString() }
        ),
        bottomAxis = rememberBottomAxis(
            label = labelComponent,
            axis = axisLineComponent,
            guideline = null, // Omit bottom guidelines for cleaner column charts
            tick = axisLineComponent,
            valueFormatter = horizontalAxisValueFormatter
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    )
}