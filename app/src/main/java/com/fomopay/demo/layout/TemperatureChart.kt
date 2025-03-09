package com.fomopay.demo.layout

import android.view.animation.AccelerateDecelerateInterpolator
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.fomopay.demo.bean.WeatherComposition
import com.fomopay.demo.linechart.LineChartAdapter
import com.fomopay.demo.linechart.LineChartFillType
import com.fomopay.demo.linechart.LineChartView
import com.fomopay.demo.linechart.animation.MorphLineChartAnimator
import com.fomopay.demo.linechart.formatter.TemperatureFormatter
import com.fomopay.demo.linechart.touch.OnScrubListener

private val myAdapter: MyAdapter = MyAdapter()
private val formatter = TemperatureFormatter()

class MyAdapter() : LineChartAdapter() {

    private var yData = floatArrayOf()

    override val count: Int
        get() = yData.size

    override fun getItem(index: Int) = yData[index]

    override fun getY(index: Int) = yData[index]

    fun setData(yData: FloatArray) {
        this.yData = yData
        notifyDataSetChanged()
    }
}

@Composable
fun TemperatureChart(wc: WeatherComposition, modifier: Modifier = Modifier) {
    val lineColorG = MaterialTheme.colorScheme.onPrimary.toArgb()
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(wc.temperatureValue, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
//        val bgColor = MaterialTheme.colorScheme.secondaryContainer.toArgb()
        val times = wc.weather.hourly.time
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            AndroidView(
                factory = { context ->
                    LineChartView(context).apply {
                        setBackgroundColor(android.graphics.Color.parseColor("#1C1A1E")) //1C1A1E
                        yAxisValueFormatter = formatter
                        isZeroLineEnabled = false
                        gridXDivisions = times.size
                        gridYDivisions = 5
                        gridLineColor = android.graphics.Color.parseColor("#474747")
                        gridLineWidth = 2.dp.value
                        fillType = LineChartFillType.NONE
                        lineWidth = 4.dp.value
                        lineColor = lineColorG

                        isScrubEnabled = true
                        lineChartAnimator = MorphLineChartAnimator().apply {
                            duration = 2000L
                            interpolator = AccelerateDecelerateInterpolator()
                        }
                        scrubListener = OnScrubListener { value: Any? ->
                            wc.temperatureValue = value?.let { "$it°C" } ?: " "
                        }
                        adapter = myAdapter
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = {
                    myAdapter.setData(wc.weather.hourly.temperature_2m.toFloatArray())
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Hourly: ${wc.hours?.get(0)} - ${wc.hours?.get(wc.hours!!.size - 1)}",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }


        }


    }

}