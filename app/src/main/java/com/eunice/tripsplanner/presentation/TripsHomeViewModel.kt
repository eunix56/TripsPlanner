package com.eunice.tripsplanner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eunice.tripsplanner.network.GetCitiesDataSource
import com.eunice.tripsplanner.network.TripsContractImpl
import com.eunice.tripsplanner.network.model.Activity
import com.eunice.tripsplanner.network.model.City
import com.eunice.tripsplanner.network.model.ErrorResponse
import com.eunice.tripsplanner.network.model.Flight
import com.eunice.tripsplanner.network.model.Hotel
import com.eunice.tripsplanner.network.model.Trip
import com.eunice.tripsplanner.network.model.TripUploadModel
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * Created by {EUNICE BAKARE T.} on {7/24/24}
 * Email: {eunice@reach.africa}
 */

class TripsHomeViewModel: ViewModel() {
    private val tripsContract = TripsContractImpl()
    private val citiesDataSource = GetCitiesDataSource()

    val _tripUiState = MutableStateFlow(TripsUiState())
    val tripsUiState: StateFlow<TripsUiState> = _tripUiState

    val _postTripUiState = MutableStateFlow(PostTripsUiState())
    val postTripsUiState: StateFlow<PostTripsUiState> = _postTripUiState

    val _flightsUiState = MutableStateFlow(FlightsUiState())
    val flightsUiState: StateFlow<FlightsUiState> = _flightsUiState

    val _hotelsUiState = MutableStateFlow(HotelsUiState())
    val hotelsUiState: StateFlow<HotelsUiState> = _hotelsUiState

    val _activitiesUiState = MutableStateFlow(ActivitiesUiState())
    val activitiesUiState: StateFlow<ActivitiesUiState> = _activitiesUiState

    var cityName: String? = null
    lateinit var startDate: String
    lateinit var endDate: String

    var userTrip: Trip? = null

    private val _cityUiNames =
        MutableStateFlow(CityUiState())
    val cityUiNames: StateFlow<CityUiState> =
        _cityUiNames

    private var cityUiNameJob: Job? = null

    private val cityUiExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _cityUiNames.update { it.copy(
            loading = false,
            error = throwable.getErrorMsg()?.message
        )
        }
    }

    private val postTripsExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _postTripUiState.update { it.copy(
            loading = false,
            error = throwable.getErrorMsg()?.message
        )
        }
    }

    private val tripsExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _tripUiState.update { it.copy(
            loading = false,
            error = throwable.getErrorMsg()?.message
        )
        }
    }

    private val flightsExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _flightsUiState.update { it.copy(
            loading = false,
            error = throwable.getErrorMsg()?.message
        )
        }
    }

    private val hotelsExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _hotelsUiState.update { it.copy(
            loading = false,
            error = throwable.getErrorMsg()?.message
        )
        }
    }

    private val activitiesExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _activitiesUiState.update { it.copy(
            loading = false,
            error = throwable.getErrorMsg()?.message
        )
        }
    }

    fun getTripStartDate(): String =
        if (::startDate.isInitialized) startDate else "25-04-2024"

    fun getTripEndDate(): String = if (::endDate.isInitialized) endDate else "25-04-2024"

    fun getCity(): String? = cityName

    fun setCity(selectedCity: String?) {
        cityName = selectedCity
    }

    fun setTrip(postedTrip: Trip) {
        userTrip = postedTrip
    }

    fun getTrip(): Trip? {
        return userTrip
    }

    fun getTrips() {
        _tripUiState.update { it.copy(loading = true) }

        viewModelScope.launch(tripsExceptionHandler) {
            val trip = tripsContract.getTrips()
            _tripUiState.update { it.copy(loading = false, success = trip) }
        }
    }


    fun postTrip(name: String,
                 travelStyle: String,
                 location: String,
                 startDate: String,
                 endDate: String) {
        _postTripUiState.update { it.copy(loading = true) }

        viewModelScope.launch(postTripsExceptionHandler) {
            val trip = tripsContract.postTrips(
                TripUploadModel(
                    name, travelStyle, location, startDate, endDate
                )
            )
            _postTripUiState.update { it.copy(loading = false, success = trip) }
        }
    }

    fun getCities(name: String) {
        cityUiNameJob = viewModelScope.launch(cityUiExceptionHandler) {
            _cityUiNames.update { it.copy(loading = true) }
            val cityNames = citiesDataSource.getCities(name)
            _cityUiNames.update {
                it.copy(loading = false, success = cityNames)
            }
        }
        cityUiNameJob?.start()
    }

    fun cancelJob() {
        cityUiNameJob?.cancel()
    }


    fun getFlights() {
        _flightsUiState.update { it.copy(loading = true) }

        viewModelScope.launch(flightsExceptionHandler) {
            val flights = tripsContract.getFlights()
            _flightsUiState.update { it.copy(loading = false, success = flights.flights) }
        }
    }

    fun getHotels() {
        _hotelsUiState.update { it.copy(loading = true) }

        viewModelScope.launch(hotelsExceptionHandler) {
            val hotels = tripsContract.getHotels()
            _hotelsUiState.update { it.copy(loading = false, success = hotels.hotels) }
        }
    }

    fun getActivities() {
        _activitiesUiState.update { it.copy(loading = true) }

        viewModelScope.launch(activitiesExceptionHandler) {
            val activity = tripsContract.getActivities()
            _activitiesUiState.update { it.copy(loading = false, success = activity.activities) }
        }
    }


}

data class TripsUiState(
    val loading: Boolean = false,
    val success: List<Trip> = emptyList(),
    val error: String? = null
)

data class PostTripsUiState(
    val loading: Boolean = false,
    val success: Trip? = null,
    val error: String? = null
)

data class FlightsUiState(
    val loading: Boolean = false,
    val success: List<Flight> = emptyList(),
    val error: String? = null
)

data class HotelsUiState(
    val loading: Boolean = false,
    val success: List<Hotel> = emptyList(),
    val error: String? = null
)

data class ActivitiesUiState(
    val loading: Boolean = false,
    val success: List<Activity> = emptyList(),
    val error: String? = null
)

data class CityUiState(
    val loading: Boolean = false,
    val success: List<City> = emptyList(),
    val error: String? = null
)

fun Throwable.getErrorMsg(): ErrorResponse? {
    val errorMsg = "An error occurred"
    return if (this is HttpException) {
        // Kotlin will smart cast at this point
        val errorJsonString = response()?.errorBody()?.string()
        try {
            errorJsonString?.let { Moshi.Builder().add(KotlinJsonAdapterFactory())
                .build().adapter(ErrorResponse::class.java).fromJson(it) }
        } catch (e: JsonDataException) {
            e.localizedMessage?.let { ErrorResponse(e.message, it) }
        }

    } else {
        ErrorResponse(null, message ?: errorMsg)
    }
}