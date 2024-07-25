package com.eunice.tripsplanner.network

import com.eunice.tripsplanner.network.model.CitiesResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by {EUNICE BAKARE T.} on {7/25/24}
 * Email: {eunice@reach.africa}
 */

const val CITIES_BASE_URL = "https://wft-geo-db.p.rapidapi.com/"

interface GetCitiesService {

    @GET("v1/geo/places")
    fun getCities(@Query("namePrefix") namePrefix: String): Flow<CitiesResponse>
}

class GetCitiesDataSource {
    private val okHttpClient = OkHttpClient.Builder().build()

    private val networkApi =
        Retrofit.Builder()
            .baseUrl(CITIES_BASE_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .client(okHttpClient)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory()).build()
                ).asLenient())
            .build()
            .create(GetCitiesService::class.java)

    fun getCities(namePrefix: String): Flow<CitiesResponse> =
        networkApi.getCities(namePrefix).flowOn(Dispatchers.IO)
}