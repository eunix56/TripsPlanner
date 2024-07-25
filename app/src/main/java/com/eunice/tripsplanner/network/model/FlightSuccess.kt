package com.eunice.tripsplanner.network.model

import com.squareup.moshi.Json

/**
 * Created by {EUNICE BAKARE T.} on {7/24/24}
 * Email: {eunice@reach.africa}
 */

class FlightSuccess(
    val status: String,
    val flights: List<Flight>,
)

class Flight(
    val id: Int,
    val name: String,
    val code: String,
    @Json(name="departure_id")
    val departureId: String,
    @Json(name="arrival_id")
    val arrivalId: String,
    @Json(name="outbound_date")
    val outboundDate: String,
    @Json(name="departure_time")
    val departureTime: String,
    @Json(name="arrival_time")
    val arrivalTime: String,
    val price: String
)