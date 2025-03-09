import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elvishew.xlog.XLog
import com.fomopay.demo.bean.Weather
import com.fomopay.demo.bean.WeatherComposition
import com.fomopay.demo.repo.WeatherApiRepository
import com.fomopay.demo.vm.WeatherUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class WeatherViewModel(context: Context) : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState
    var expanded by mutableStateOf(false)
    var selectedIndex by mutableIntStateOf(0)

    private val apiService = WeatherApiRepository.create()
    private val locationRepository = LocationRepository(context)

    private var limitItemSize = 5

    private val weatherComposition = WeatherComposition()

    init {
        locationRepository.setCallback { fetchWeatherDataWithLimitSize(limitItemSize) }
    }

    private fun limitSize(w: Weather, size: Int = 5): Weather {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val calendar = Calendar.getInstance()
        calendar.get(Calendar.HOUR_OF_DAY)
        // Get the current date and time
        val currentDateTime = LocalDateTime.now()
        val currentHour = currentDateTime.withMinute(0).withSecond(0).withNano(0)

        val hours = w.hourly.time
        val temperatures = w.hourly.temperature_2m
        var idx = 0
        var record = 0
        for (t in hours) {
            val localDateTime = LocalDateTime.parse(t, formatter)
            if (currentHour.isEqual(localDateTime) || currentHour.isBefore(localDateTime)) {
                record = idx
                break
            }
            idx++
        }

        val toIndex = if (record + size > hours.size) {
            hours.size
        } else {
            record + size
        }

        w.hourly.time = hours.subList(record, toIndex)
        w.hourly.temperature_2m = temperatures.subList(record, toIndex)

        return w
    }

    private fun fetchWeatherDataFromApi(latitude: Double, longitude: Double, limitSize: Int = 5) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (_weatherState.value != WeatherUiState.Loading) {
                    _weatherState.value = WeatherUiState.Loading
                }
                val response = apiService.getWeatherData(latitude, longitude)
                weatherComposition.weather = response
                limitSize(response, limitSize)
                val timeList = response.hourly.time
                weatherComposition.hours = listOf(timeList.first(), timeList.last()).map {
                    it.substring(
                        it.length - 5, it.length
                    )
                }
                _weatherState.value = WeatherUiState.Success(weatherComposition)
            } catch (e: HttpException) {
                _weatherState.value =
                    WeatherUiState.Error("HTTP error, please refresh")
                XLog.e("HTTP error", e)
            } catch (e: IOException) {
                _weatherState.value =
                    WeatherUiState.Error("Network error, please refresh")
                XLog.e("Network error", e)
            }
        }
    }

    fun fetchWeatherDataWithLimitSize(limitSize: Int) {
        limitItemSize = limitSize
        locationRepository.startLastLocation()
        val location = locationRepository.getLocation()
        if (location == null) {
            _weatherState.value =
                WeatherUiState.Error("Cannot access location, please check permissions")
            return
        }
        fetchWeatherDataFromApi(location.latitude, location.longitude, limitSize)

//        fetchWeatherDataFromApi(52.52, 13.41, limitSize = limitItemSize)
    }

    fun fetchWeatherData() {
        fetchWeatherDataWithLimitSize(limitItemSize)
    }
}

