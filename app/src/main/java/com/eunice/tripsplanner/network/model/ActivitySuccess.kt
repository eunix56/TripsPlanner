package com.eunice.tripsplanner.network.model

/**
 * Created by {EUNICE BAKARE T.} on {7/24/24}
 * Email: {eunice@reach.africa}
 */

data class ActivitySuccess(
    val status: String,
    val activities: List<Activity>,
)

data class Activity(
    val id: Int,
    val name: String,
    val description: String,
    val city: String,
    val country: String,
    val datetime: String,
    val day: String,
    val price: String
)