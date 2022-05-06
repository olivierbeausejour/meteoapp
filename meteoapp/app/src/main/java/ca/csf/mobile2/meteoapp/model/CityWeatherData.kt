package ca.csf.mobile2.meteoapp.model

import com.fasterxml.jackson.databind.JsonNode

private const val CITY_NODE_KEY = "name"
private const val SYS_NODE_KEY = "sys"
private const val SYS_NODE_COUNTRY_KEY = "country"
private const val WEATHER_NODE_KEY = "weather"
private const val WEATHER_NODE_MAIN_KEY = "main"
private const val MAIN_NODE_KEY = "main"
private const val MAIN_NODE_TEMP_KEY = "temp"

const val TYPE_THUNDERSTORM = "Thunderstorm"
const val TYPE_DRIZZLE = "Drizzle"
const val TYPE_RAIN = "Rain"
const val TYPE_SNOW = "Snow"
const val TYPE_MIST = "Mist"
const val TYPE_SMOKE = "Smoke"
const val TYPE_HAZE = "Haze"
const val TYPE_DUST = "Dust"
const val TYPE_FOG = "Fog"
const val TYPE_SAND = "Sand"
const val TYPE_ASH = "Ash"
const val TYPE_SQUALL = "Squall"
const val TYPE_TORNADO = "Tornado"
const val TYPE_CLEAR = "Clear"
const val TYPE_CLOUDS = "Clouds"


class CityWeatherData(private val objectNode: JsonNode) {

    var city : String = ""
    var country : String = ""
    var type : String = ""
    var temperatureInCelsius : Int = 0


    fun parseData() {
        city = objectNode.get(CITY_NODE_KEY).asText()

        val sysNode : JsonNode = objectNode.get(SYS_NODE_KEY)
        country = sysNode.get(SYS_NODE_COUNTRY_KEY).asText()

        val weatherNode : JsonNode = objectNode.get(WEATHER_NODE_KEY).get(0)
        type = weatherNode.get(WEATHER_NODE_MAIN_KEY).asText()

        val mainNode : JsonNode = objectNode.get(MAIN_NODE_KEY)
        temperatureInCelsius = mainNode.get(MAIN_NODE_TEMP_KEY).asInt()
    }
}