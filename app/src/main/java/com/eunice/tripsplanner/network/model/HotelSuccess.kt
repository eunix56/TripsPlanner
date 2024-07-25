package com.eunice.tripsplanner.network.model

import com.squareup.moshi.Json

/**
 * Created by {EUNICE BAKARE T.} on {7/24/24}
 * Email: {eunice@reach.africa}
 */

class HotelSuccess(
    val status: String,
    val hotels: List<Hotel>,
)

class Hotel(
    val id: Int,
    val name: String,
    val address: String,
    val rating: String,
    @Json(name="no_of_ratings")
    val noOfRatings: String,
    @Json(name="room_type")
    val roomType: String,
    @Json(name="price_per_night")
    val pricePerNight: String,
)