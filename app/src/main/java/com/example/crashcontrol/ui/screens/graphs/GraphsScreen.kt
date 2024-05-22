package com.example.crashcontrol.ui.screens.graphs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crashcontrol.R
import com.example.crashcontrol.data.database.Crash
import com.mahmoud.composecharts.barchart.BarChart
import com.mahmoud.composecharts.barchart.BarChartEntity

@Composable
fun GraphsScreen(
    crashes: List<Crash>
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (crashes.isNotEmpty()) {
//        val orderedDates = crashes.map { it.date }.distinct().sortedBy { it }
//        val crashesPerDayCount = crashes.groupBy { it.date }.map { it.value.size.toFloat() }
//        val verticalverticalAxisValues =
//            (0..crashesPerDayCount.max().toInt()).map { it.toFloat() }
//        val lineChartData = mutableListOf<LineChartEntity>()
//        crashesPerDayCount.forEachIndexed { index, crash ->
//            lineChartData.add(LineChartEntity(crash, orderedDates[orderedDates.size - index]))
//        }
//        LineChart(
//            lineChartData = lineChartData,
//            verticalAxisValues = verticalverticalAxisValues
//        )
            val colors = listOf(
                Color.Blue, Color.Red, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan
            )
            val faces = crashes.groupBy { it.face }.map { it.key }
            val crashesPerFace = crashes.groupBy { it.face }.map { it.value.size.toFloat() }
            val barChartData = mutableListOf<BarChartEntity>()
            crashesPerFace.forEachIndexed { index, crash ->
                barChartData.add(BarChartEntity(crash, colors[index], faces[index]))
            }
            val barVerticalAxisValues =
                (0..crashesPerFace.max().toInt()).map { it.toFloat() }
            Text(
                text = stringResource(R.string.faces_graph_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            BarChart(
                barChartData = barChartData,
                verticalAxisValues = barVerticalAxisValues
            )
        } else {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_crashes_graph),
                    modifier = Modifier.padding(top = 30.dp, bottom = 20.dp),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}