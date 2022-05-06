package ca.csf.mobile2.meteoapp.activity

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.Group
import ca.csf.mobile2.meteoapp.R
import ca.csf.mobile2.meteoapp.controller.MeteoActivityController
import kotlinx.android.synthetic.main.activity_meteo.*
import java.lang.StringBuilder

//State keys
const val STATE_SEARCH_RESULTS_GROUP_VISIBILITY = "STATE_SEARCH_RESULTS_GROUP_VISIBILITY"
const val STATE_ERROR_MESSAGE_GROUP_VISIBILITY = "STATE_ERROR_MESSAGE_GROUP_VISIBILITY"
const val STATE_PROGRESS_BAR_VISIBILITY = "STATE_PROGRESS_BAR_VISIBILITY"
const val STATE_ASYNC_TASK_ACTIVE = "STATE_ASYNC_TASK_ACTIVE"
const val STATE_SEARCH_BAR_EDIT_TEXT_INPUT = "STATE_SEARCH_BAR_TEXT"
const val STATE_WEATHER_TYPE_IMAGE_ID = "STATE_WEATHER_TYPE_IMAGE_ID"
const val STATE_TEMPERATURE_IN_CELSIUS = "STATE_TEMPERATURE_IN_CELSIUS"
const val STATE_LOCATION_NAME = "STATE_LOCATION_NAME"
const val STATE_ERROR_MESSAGE = "STATE_ERROR_MESSAGE"

const val DEFAULT_WEATHER_IMAGE_VIEW_ID = R.drawable.ic_sunny
const val HIDE_KEYBOARD_INPUT_MANAGER_FLAG = 0

class MeteoActivity : AppCompatActivity() {

    //Groups
    private lateinit var searchResultsGroup : Group
    private lateinit var errorMessageGroup : Group

    //Progress bar
    private lateinit var progressBar : ProgressBar

    //Search bar
    private lateinit var searchBarEditText : EditText

    //Search results
    private lateinit var weatherImageView : ImageView
    private lateinit var temperatureTextView : TextView
    private lateinit var locationNameTextView : TextView

    //Error message
    private lateinit var errorImageView : ImageView
    private lateinit var errorMessageTextView : TextView
    private lateinit var errorMessageRetryButton : Button

    //Controller
    private val controller = MeteoActivityController(this)

    //Weather ImageView id
    private var weatherImageId : Int = DEFAULT_WEATHER_IMAGE_VIEW_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meteo)

        searchResultsGroup = findViewById(R.id.search_results_group)
        errorMessageGroup = findViewById(R.id.error_message_group)

        searchBarEditText = findViewById(R.id.search_bar_edit_text)

        weatherImageView = findViewById(R.id.weather_image_view)
        temperatureTextView = findViewById(R.id.temperature_text_view)
        locationNameTextView = findViewById(R.id.location_name_text_view)

        errorImageView = findViewById(R.id.error_image_view)
        errorMessageTextView = findViewById(R.id.error_message_text_view)
        errorMessageRetryButton = findViewById(R.id.error_message_retry_button)

        progressBar = findViewById(R.id.progress_bar)

        searchBarEditText.onSubmit { submitCityName(null) }
        errorMessageRetryButton.setOnClickListener(this::submitCityName)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putInt(STATE_SEARCH_RESULTS_GROUP_VISIBILITY, searchResultsGroup.visibility)
        outState?.putInt(STATE_ERROR_MESSAGE_GROUP_VISIBILITY, errorMessageGroup.visibility)
        outState?.putInt(STATE_PROGRESS_BAR_VISIBILITY, progressBar.visibility)
        outState?.putBoolean(STATE_ASYNC_TASK_ACTIVE, controller.asyncTask.status == AsyncTask.Status.RUNNING)
        outState?.putString(STATE_SEARCH_BAR_EDIT_TEXT_INPUT, searchBarEditText.text.toString())
        outState?.putInt(STATE_WEATHER_TYPE_IMAGE_ID, weatherImageId)
        outState?.putString(STATE_TEMPERATURE_IN_CELSIUS, temperatureTextView.text.toString())
        outState?.putString(STATE_LOCATION_NAME, locationNameTextView.text.toString())
        outState?.putString(STATE_ERROR_MESSAGE, errorMessageTextView.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        setGroupVisibilities(
            savedInstanceState?.getInt(STATE_SEARCH_RESULTS_GROUP_VISIBILITY)!!,
            savedInstanceState?.getInt(STATE_ERROR_MESSAGE_GROUP_VISIBILITY)!!,
            savedInstanceState?.getInt(STATE_PROGRESS_BAR_VISIBILITY)!!)

        search_bar_edit_text.setText(savedInstanceState.getString(STATE_SEARCH_BAR_EDIT_TEXT_INPUT))

        setWeatherValues(
            savedInstanceState?.getString(STATE_LOCATION_NAME)!!,
            savedInstanceState?.getInt(STATE_WEATHER_TYPE_IMAGE_ID)!!,
            savedInstanceState?.getString(STATE_TEMPERATURE_IN_CELSIUS)!!)

        setErrorMessage(savedInstanceState?.getString(STATE_ERROR_MESSAGE)!!)

        if (savedInstanceState?.getBoolean(STATE_ASYNC_TASK_ACTIVE)!!) {
            submitCityName(null)
        }
    }

    private fun submitCityName(view : View?) {
        val searchInput = searchBarEditText.text.toString()
        if (searchInput.isNotEmpty()) {
            controller.fetchCityWeatherData(searchInput)
        }
    }

    fun hideKeyBoard() {
        val view : View = findViewById(android.R.id.content)
        val inputManager : InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputManager.hideSoftInputFromWindow(view.windowToken, HIDE_KEYBOARD_INPUT_MANAGER_FLAG)
    }

    fun setGroupVisibilities(searchResultsVisibility : Int, errorMessageVisibility : Int,
                                     progressBarVisibility : Int) {
        searchResultsGroup.visibility = searchResultsVisibility
        errorMessageGroup.visibility = errorMessageVisibility
        progressBar.visibility = progressBarVisibility
    }

    fun setWeatherValues(location : String, weatherTypeImageId: Int, temperatureInCelsius: String) {
        locationNameTextView.text = location
        weatherImageId = weatherTypeImageId
        weather_image_view.setImageResource(weatherTypeImageId)
        temperatureTextView.text = temperatureInCelsius
    }

    fun setWeatherValues(city : String, country : String, weatherTypeImageId : Int, temperatureInCelsius : String) {
        setWeatherValues(StringBuilder().append("$city, $country").toString(), weatherTypeImageId, temperatureInCelsius)
    }

    fun setErrorMessage(messageId : Int) {
        errorMessageTextView.setText(messageId)
    }

    fun setErrorMessage(messageString : String) {
        errorMessageTextView.text = messageString
    }

    fun EditText.onSubmit(function: () -> Unit) { setOnEditorActionListener { _, actionDone, _ ->
            if (actionDone == EditorInfo.IME_ACTION_DONE) {
                function()
            }
            true
        }
    }
}
