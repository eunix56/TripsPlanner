package com.eunice.tripsplanner.network

import com.eunice.tripsplanner.network.model.ActivitySuccess
import com.eunice.tripsplanner.network.model.FlightSuccess
import com.eunice.tripsplanner.network.model.HotelSuccess
import com.eunice.tripsplanner.network.model.Trip
import com.eunice.tripsplanner.network.model.TripUpdate
import com.eunice.tripsplanner.network.model.TripUpdateSuccess
import com.eunice.tripsplanner.network.model.TripUploadModel
import kotlinx.coroutines.flow.Flow

/**
 * Created by {EUNICE BAKARE T.} on {7/24/24}
 * Email: {eunice@reach.africa}
 */

interface TripsContract {
    suspend fun postTrips(tripUploadModel: TripUploadModel): Trip

    fun getTrips(): Flow<List<Trip>>

    suspend fun updateTrip(id: String, tripUpdate: TripUpdate): TripUpdateSuccess

    suspend fun getFlights(): FlightSuccess

    suspend fun getHotels(): HotelSuccess

    suspend fun getActivities(): ActivitySuccess
}