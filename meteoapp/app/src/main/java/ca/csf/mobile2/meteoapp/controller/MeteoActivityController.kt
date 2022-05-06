package ca.csf.mobile2.meteoapp.controller

import android.view.View
import ca.csf.mobile2.meteoapp.R
import ca.csf.mobile2.meteoapp.activity.MeteoActivity
import ca.csf.mobile2.meteoapp.model.*

class MeteoActivityController(private val activity : MeteoActivity) {

    //Async task
    var asyncTask = FetchCityWeatherDataAsyncTask(this::onSuccess, this::onError)

    fun fetchCityWeatherData(cityName : String) {
        activity.hideKeyBoard()
        activity.setGroupVisibilities(View.GONE,  View.GONE,  View.VISIBLE)

        asyncTask = FetchCityWeatherDataAsyncTask(this::onSuccess, this::onError)
        asyncTask.execute(cityName)
    }

    private fun onSuccess(weatherData: CityWeatherData) {
        activity.setGroupVisibilities( View.VISIBLE,  View.GONE,  View.GONE)
        activity.setWeatherValues(weatherData.city, weatherData.country, convertWeatherTypeToImageId(weatherData.type),
            weatherData.temperatureInCelsius.toString() + activity.getString(R.string.temperature_measurement_unit))
    }

    private fun onError(networkError: NetworkError) {
        activity.setGroupVisibilities( View.GONE,  View.VISIBLE,  View.GONE)
        activity.setErrorMessage(convertErrorEnumToStringId(networkError))
    }

    private fun convertWeatherTypeToImageId (weatherType : String) : Int {
        return when (weatherType) {
            TYPE_THUNDERSTORM -> R.drawable.ic_thunder
            TYPE_DRIZZLE, TYPE_RAIN -> R.drawable.ic_rain
            TYPE_SNOW -> R.drawable.ic_snow
            TYPE_MIST, TYPE_SMOKE, TYPE_HAZE, TYPE_DUST, TYPE_FOG, TYPE_SAND, TYPE_ASH, TYPE_SQUALL, TYPE_TORNADO -> R.drawable.ic_mist
            TYPE_CLEAR -> R.drawable.ic_sunny
            TYPE_CLOUDS -> R.drawable.ic_cloudy
            else -> R.drawable.ic_cloudy
        }
    }

    private fun convertErrorEnumToStringId (networkError: NetworkError) : Int {
        return when(networkError) {
            NetworkError.Unauthorized -> R.string.error_message_unauthorized
            NetworkError.Forbidden -> R.string.error_message_forbidden
            NetworkError.NotFound -> R.string.error_message_not_found
            NetworkError.Unknown -> R.string.error_message_unknown
        }
    }
}