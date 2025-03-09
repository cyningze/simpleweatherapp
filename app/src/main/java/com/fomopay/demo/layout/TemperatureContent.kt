package com.fomopay.demo.layout

import WeatherViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fomopay.demo.bean.WeatherData
import com.fomopay.demo.vm.WeatherUiState

private val tabTitles = listOf("Line Chart", "Table")

@Composable
fun WeatherContent(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        val weatherState by viewModel.weatherState.collectAsState()
        when (weatherState) {
            is WeatherUiState.Loading -> LoadingSkeleton()
            is WeatherUiState.Success -> {
                val wc = (weatherState as WeatherUiState.Success).weatherComposition
                TabRow(
                    selectedTabIndex = wc.pageIndex,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(text = title) },
                            selected = wc.pageIndex == index,
                            onClick = {
                                if (index == 0) {
                                    wc.visibleIndices.intValue = -1
                                }
                                wc.pageIndex = index
                            }
                        )
                    }
                }
                if (wc.pageIndex == 0) {
                    TemperatureChart(wc)
                } else {
                    TemperatureTable(wc)
                }
            }

            is WeatherUiState.Error -> ErrorMessage(
                (weatherState as WeatherUiState.Error).message
            )
        }
    }
}

private val items =
    listOf(WeatherData(5, "5 hours"), WeatherData(10, "10 hours"), WeatherData(24, "1 day"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherTopBar(viewModel: WeatherViewModel) {
    TopAppBar(
        title = {
            Text(text = "WEATHER")
        }, actions = {

            // A clickable text that triggers the dropdown menu
            ExposedDropdownMenuBox(
                expanded = viewModel.expanded,
                onExpandedChange = { viewModel.expanded = !it }
            ) {
                IconButton(
                    onClick = { viewModel.expanded = true },
                    modifier = Modifier
                        .menuAnchor()
                        .width(100.dp)
                ) {
                    Text(text = items[viewModel.selectedIndex].name)
                }
                ExposedDropdownMenu(
                    expanded = viewModel.expanded,
                    onDismissRequest = { viewModel.expanded = false }
                ) {
                    items.forEachIndexed { idx, item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = item.name,
                                    Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            onClick = {
                                viewModel.fetchWeatherDataWithLimitSize(items[idx].limitSize)
                                viewModel.selectedIndex = idx
                                viewModel.expanded = false
                            }
                        )
                    }
                }
            }
            IconButton(onClick = {
                if (viewModel.weatherState.value != WeatherUiState.Loading) {
                    viewModel.fetchWeatherData()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }
    )
}