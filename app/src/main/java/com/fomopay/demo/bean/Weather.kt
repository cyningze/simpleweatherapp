package com.fomopay.demo.bean

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


data class Hourly(
    var time: List<String>,
    var temperature_2m: List<Float>
)

data class Weather(
    val hourly: Hourly
)

data class WeatherData(val limitSize: Int, val name: String)

class WeatherComposition() {
    var temperatureValue by mutableStateOf(" ")
    var pageIndex by mutableIntStateOf(0)
    val visibleIndices = mutableIntStateOf(-1)
    var hours: List<String>? = null


    lateinit var weather: Weather
}
