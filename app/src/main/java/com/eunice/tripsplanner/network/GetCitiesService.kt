package com.eunice.tripsplanner.network


import com.eunice.tripsplanner.network.model.City
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Created by {EUNICE BAKARE T.} on {7/25/24}
 * Email: {eunice@reach.africa}
 */

const val API_KEY = "s1jiQlkcnNGdmn0MjwOvvQ==bpPhu8SeUHAq6T1l"
const val CITIES_BASE_URL = "https://api.api-ninjas.com/"

interface GetCitiesService {

    @GET("v1/city")
    @Headers("X-API-KEY: $API_KEY")
    suspend fun getCities(@Query("name") namePrefix: String,
                  @Query("limit") limit: Int = 5): List<City>
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

    suspend fun getCities(namePrefix: String): List<City> {
        return withContext(Dispatchers.IO) {
            networkApi.getCities(namePrefix)
        }
    }
}