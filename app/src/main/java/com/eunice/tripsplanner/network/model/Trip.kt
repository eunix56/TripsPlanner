package com.eunice.tripsplanner.network.model

import com.squareup.moshi.Json

/**
 * Created by {EUNICE BAKARE T.} on {7/24/24}
 * Email: {eunice@reach.africa}
 */

data class Trip(
    val id: String,
    val name: String,
    @Json(name = "travel_style")
    val travelStyle: String,
    val location: String,
    @Json(name = "start_date")
    val startDate: String,
    @Json(name = "end_date")
    val endDate: String,
    val hotel: Hotel?,
    val flight: Flight?,
    val activity: Activity?
)