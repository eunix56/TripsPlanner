package com.eunice.tripsplanner.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by {EUNICE BAKARE T.} on {7/24/24}
 * Email: {eunice@reach.africa}
 */


@JsonClass(generateAdapter = true)
data class Trip(
    val id: String,
    val name: String,
    @Json(name = "travel_style")
    val travelStyle: String,
    val location: String,
    @Json(name = "start_date")
    val startDate: String,
    @Json(name = "end_date")
    val endDate: String
)
