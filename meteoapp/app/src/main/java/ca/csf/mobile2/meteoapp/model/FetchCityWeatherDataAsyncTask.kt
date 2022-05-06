package ca.csf.mobile2.meteoapp.model

import android.os.AsyncTask
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import java.lang.StringBuilder
import java.net.HttpURLConnection

const val WEATHER_SERVICE_URL = "http://api.openweathermap.org/data/2.5/weather"
const val WEATHER_SERVICE_CITY_KEY = "?q="
const val WEATHER_SERVICE_UNIT_KEY = "&units="
const val WEATHER_SERVICE_UNIT = "metric"
const val WEATHER_SERVICE_API_ID = "&APPID=7b2925b537dbfc7b30b01d28e3e7a71c"

class FetchCityWeatherDataAsyncTask(val onSuccess : (CityWeatherData) -> Unit,
                                    val onServerError : (NetworkError) -> Unit) : AsyncTask<String, Unit, Promise<CityWeatherData?, NetworkError?>>() {

    override fun doInBackground(vararg params: String?): Promise<CityWeatherData?, NetworkError?> {

        val okHttpClient = OkHttpClient()
        val url = StringBuilder().append(WEATHER_SERVICE_URL + WEATHER_SERVICE_CITY_KEY + params[0] + WEATHER_SERVICE_UNIT_KEY + WEATHER_SERVICE_UNIT + WEATHER_SERVICE_API_ID).toString()
        val request = Request.Builder().url(url).build()

        try {
            val response = okHttpClient.newCall(request).execute()

            if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                return Promise.err(NetworkError.Unauthorized)
            }

            else if (response.code == HttpURLConnection.HTTP_FORBIDDEN) {
                return Promise.err(NetworkError.Forbidden)
            }

            else if (response.code == HttpURLConnection.HTTP_NOT_FOUND) {
                return Promise.err(NetworkError.NotFound)
            }

            val json = response.body!!.string()
            val mapper = jacksonObjectMapper()
            val objectNode : JsonNode = mapper.readValue(json)
            val cityWeatherData = CityWeatherData(objectNode)
            cityWeatherData.parseData()
            return Promise.ok(cityWeatherData)
        }

        catch (ex : Exception) {
            return Promise.err(NetworkError.Unknown)
        }
    }

    override fun onPostExecute(result: Promise<CityWeatherData?, NetworkError?>) {
        if (result.isSuccessful) {
            onSuccess(result.result!!)
        }

        else {
            onServerError(result.error!!)
        }
    }
}