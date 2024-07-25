package com.eunice.tripsplanner.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by {EUNICE BAKARE T.} on {7/24/24}
 * Email: {eunice@reach.africa}
 */

@JsonClass(generateAdapter = true)
class TripUploadModel(
    val name: String,
    @Json(name = "travel_style")
    val travelStyle: String,
    val location: String,
    @Json(name = "start_date")
    val startDate: String,
    @Json(name = "end_date")
    val endDate: String
)

@JsonClass(generateAdapter = true)
class TripUpdate(
    val hotel: Hotel?,
    val flight: Flight?,
    val activity: Activity?
)

@JsonClass(generateAdapter = true)
class TripUpdateSuccess(
    val id: String,
    val hotel: Hotel?,
    val flight: Flight?,
    val activity: Activity?
)