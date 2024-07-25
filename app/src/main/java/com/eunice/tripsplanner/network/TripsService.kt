package com.eunice.tripsplanner.network

import com.eunice.tripsplanner.network.model.ActivitySuccess
import com.eunice.tripsplanner.network.model.FlightSuccess
import com.eunice.tripsplanner.network.model.HotelSuccess
import com.eunice.tripsplanner.network.model.Trip
import com.eunice.tripsplanner.network.model.TripUpdate
import com.eunice.tripsplanner.network.model.TripUpdateSuccess
import com.eunice.tripsplanner.network.model.TripUploadModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by {EUNICE BAKARE T.} on {7/24/24}
 * Email: {eunice@reach.africa}
 */
const val BASE_URL = "https://ca390c5dcd58519c752a.free.beeceptor.com/"
interface TripsService {
    @GET("api/trips")
    suspend fun getTrips(): List<Trip>

    @POST("api/trips")
    suspend fun postTrip(@Body tripResponse: TripUploadModel): Trip

    @GET("api/flights")
    suspend fun getFlights(): FlightSuccess

    @GET("api/hotels")
    suspend fun getHotels(): HotelSuccess

    @GET("api/activities")
    suspend fun getActivities(): ActivitySuccess

    @PATCH("api/trips/{id}")
    suspend fun updateTrip(@Path("id") id: String, @Body tripUpdate: TripUpdate): TripUpdateSuccess
}

class TripsContractImpl : TripsContract {

    private val okHttpClient = OkHttpClient.Builder().build()

    private val converterFactory = MoshiConverterFactory.create(Moshi.Builder()
        .add(KotlinJsonAdapterFactory()).build()).asLenient()

    private val networkApi =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
            .create(TripsService::class.java)

    override suspend fun postTrips(tripUploadModel: TripUploadModel): Trip {
        return withContext(Dispatchers.IO) {
            networkApi.postTrip(tripUploadModel)
        }
    }

    override suspend fun getTrips(): List<Trip> {
        return withContext(Dispatchers.IO) {
            networkApi.getTrips()
        }
    }

    override suspend fun updateTrip(id: String, tripUpdate: TripUpdate): TripUpdateSuccess {
        return withContext(Dispatchers.IO) {
            networkApi.updateTrip(id, tripUpdate)
        }
    }

    override suspend fun getFlights(): FlightSuccess {
        return withContext(Dispatchers.IO) {
            networkApi.getFlights()
        }
    }

    override suspend fun getHotels(): HotelSuccess {
        return withContext(Dispatchers.IO) {
            networkApi.getHotels()
        }
    }

    override suspend fun getActivities(): ActivitySuccess {
        return withContext(Dispatchers.IO) {
            networkApi.getActivities()
        }
    }


}