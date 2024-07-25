package com.eunice.tripsplanner.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by {EUNICE BAKARE T.} on {7/25/24}
 * Email: {eunice@reach.africa}
 */


@JsonClass(generateAdapter = true)
class ErrorResponse(
    val code: String?,
    val message: String
)

@JsonClass(generateAdapter = true)
class City(
    val name: String,
    val country: String
)