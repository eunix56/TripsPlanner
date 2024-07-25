package com.eunice.tripsplanner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eunice.tripsplanner.network.GetCitiesDataSource
import com.eunice.tripsplanner.network.TripsContractImpl
import com.eunice.tripsplanner.network.model.Activity
import com.eunice.tripsplanner.network.model.CitiesResponse
import com.eunice.tripsplanner.network.model.ErrorResponse
import com.eunice.tripsplanner.network.model.Flight
import com.eunice.tripsplanner.network.model.Hotel
import com.eunice.tripsplanner.network.model.Trip
import com.eunice.tripsplanner.network.model.TripUploadModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
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

    val _postTripUiState = MutableStateFlow(PostTripsUiState())
    val postTripsUiState: StateFlow<PostTripsUiState> = _postTripUiState

    val _flightsUiState = MutableStateFlow(FlightsUiState())
    val flightsUiState: StateFlow<FlightsUiState> = _flightsUiState

    val _hotelsUiState = MutableStateFlow(HotelsUiState())
    val hotelsUiState: StateFlow<HotelsUiState> = _hotelsUiState

    val _activitiesUiState = MutableStateFlow(ActivitiesUiState())
    val activitiesUiState: StateFlow<ActivitiesUiState> = _activitiesUiState

    val _namePrefix = MutableStateFlow(String())

    var city: String? = null
    lateinit var startDate: String
    lateinit var endDate: String

    private val postTripsExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _postTripUiState.update { it.copy(
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

    fun setCity(selectedCity: String?) {
        city = selectedCity
    }

    fun setName(name: String) {
        _namePrefix.value = name
    }

    fun getTrips(): Flow<List<Trip>> =
        tripsContract.getTrips().catch {
            it.getErrorMsg()
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
            _postTripUiState.update { it.copy(success = trip) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCities(): Flow<CitiesResponse> =
        _namePrefix.flatMapLatest {
            citiesDataSource.getCities(it)
        }.catch {
            it.getErrorMsg()
        }

    fun getFlights() {
        _flightsUiState.update { it.copy(loading = true) }

        viewModelScope.launch(flightsExceptionHandler) {
            val flights = tripsContract.getFlights()
            _flightsUiState.update { it.copy(success = flights.flights) }
        }
    }

    fun getHotels() {
        _hotelsUiState.update { it.copy(loading = true) }

        viewModelScope.launch(hotelsExceptionHandler) {
            val hotels = tripsContract.getHotels()
            _hotelsUiState.update { it.copy(success = hotels.hotels) }
        }
    }

    fun getActivities() {
        _activitiesUiState.update { it.copy(loading = true) }

        viewModelScope.launch(activitiesExceptionHandler) {
            val activity = tripsContract.getActivities()
            _activitiesUiState.update { it.copy(success = activity.activities) }
        }
    }


}

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

fun Throwable.getErrorMsg(): ErrorResponse? {
    val errorMsg = "An error occurred"
    return if (this is HttpException) {
        // Kotlin will smart cast at this point
        val errorJsonString = response()?.errorBody()?.string()
        errorJsonString?.let { Moshi.Builder().add(KotlinJsonAdapterFactory())
            .build().adapter(ErrorResponse::class.java).fromJson(it) }
    } else {
        ErrorResponse(null, message ?: errorMsg)
    }
}