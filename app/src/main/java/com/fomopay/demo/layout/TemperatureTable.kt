package com.fomopay.demo.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fomopay.demo.bean.WeatherComposition
import kotlinx.coroutines.time.delay
import java.time.Duration


@Composable
fun TemperatureTable(wc: WeatherComposition) {
    val hourly = wc.weather.hourly
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .height(38.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Hour",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.surfaceDim
        )
        Text(
            text = "Temperature (°C)",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.surfaceDim
        )
    }


    LaunchedEffect(Unit) {
        hourly.time.forEachIndexed { index, _ ->
            delay(Duration.ofMillis(100)) // Delay between each item's appearance
            wc.visibleIndices.intValue = index
        }
    }

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        for (i in hourly.time.indices) {
            AnimatedVisibility(
                visible = (i <= wc.visibleIndices.intValue),
                enter = fadeIn(animationSpec = tween(durationMillis = 100)) + expandVertically(
                    tween(
                        durationMillis = 100
                    )
                ),
            ) {
                Row(
                    modifier = Modifier.height(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = hourly.time[i],
                        modifier = Modifier
                            .weight(1f),
                    )
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            text = "${hourly.temperature_2m[i]}°C",
                            modifier = Modifier.width(60.dp),
                            maxLines = 1,
                        )
                    }

                }
            }

        }
    }
}
