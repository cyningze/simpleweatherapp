package com.fomopay.demo.vm

import com.fomopay.demo.bean.WeatherComposition

sealed class WeatherUiState {
    data object Loading : WeatherUiState()
    data class Success(val weatherComposition: WeatherComposition) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}