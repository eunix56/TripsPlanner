package com.eunice.tripsplanner.network.model

import com.squareup.moshi.Json

/**
 * Created by {EUNICE BAKARE T.} on {7/24/24}
 * Email: {eunice@reach.africa}
 */

class TripUploadModel(
    val name: String,
    @Json(name = "travel_style")
    val travelStyle: String,
    val location: String,
    @Json(name = "start_date")
    val startDate: String,
    @Json(name = "end_date")
    val endDate: String,
    val hotel: Hotel? = null,
    val flight: Flight? = null,
    val activity: Activity? = null
)

class TripUpdate(
    val hotel: Hotel?,
    val flight: Flight?,
    val activity: Activity?
)

class TripUpdateSuccess(
    val id: String,
    val hotel: Hotel?,
    val flight: Flight?,
    val activity: Activity?
)