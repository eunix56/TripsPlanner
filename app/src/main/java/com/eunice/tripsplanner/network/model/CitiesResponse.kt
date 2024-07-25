package com.eunice.tripsplanner.network.model

/**
 * Created by {EUNICE BAKARE T.} on {7/25/24}
 * Email: {eunice@reach.africa}
 */

class CitiesResponse(
    val data: List<City>?,
    val error: List<ErrorResponse>?
)

class ErrorResponse(
    val code: String?,
    val message: String
)

class City(
    val name: String,
    val country: String
)