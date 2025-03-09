@file:OptIn(ExperimentalMaterial3Api::class)

package com.fomopay.demo

import WeatherViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.fomopay.demo.layout.WeatherContent
import com.fomopay.demo.layout.WeatherTopBar
import com.fomopay.demo.ui.theme.WeatherappTheme

class MainActivity : ComponentActivity() {
    private lateinit var mViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        XLog.init(LogLevel.ALL)
        super.onCreate(savedInstanceState)
        mViewModel = WeatherViewModel(this)
        enableEdgeToEdge()
        setContent {
            WeatherappTheme {
                Scaffold(
                    topBar = {
                        WeatherTopBar(mViewModel)
                    }, modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    WeatherContent(mViewModel, Modifier.padding(innerPadding))
                    mViewModel.fetchWeatherData()
                }
            }
        }
    }
}



