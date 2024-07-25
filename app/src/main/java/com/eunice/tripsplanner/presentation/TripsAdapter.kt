package com.eunice.tripsplanner.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eunice.tripsplanner.databinding.ItemTripCardBinding
import com.eunice.tripsplanner.network.model.Trip
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Created by {EUNICE BAKARE T.} on {7/25/24}
 * Email: {eunice@reach.africa}
 */

const val FULL_DATE_PATTERN = "yyyy-MM-dd"

class TripsAdapter {

    class TripsViewHolder(val itemTripCardBinding: ItemTripCardBinding)
        : RecyclerView.ViewHolder(itemTripCardBinding.root) {

            fun bind(trip: Trip) = with(itemTripCardBinding) {
                val days = TimeUnit.SECONDS.toDays(
                    getLongDateTime(trip.endDate) - getLongDateTime(trip.startDate)
                )
                tvTripName.text = trip.name
                tvDate.text = trip.startDate
                tvNoOfDays.text = "$days days"
            }
    }

    companion object {
        val DIFF_UTIL = object  : DiffUtil.ItemCallback<Trip>() {
            override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
                return oldItem == newItem
            }

        }
    }
}


fun getLongDateTime(date: String): Long {
    return LocalDateTime.parse(date,
        DateTimeFormatter.ofPattern(FULL_DATE_PATTERN))
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

fun getDateAsString(timeInMillis: Long): String {
    val localDate = Instant.ofEpochMilli(timeInMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    val formatter = DateTimeFormatter.ofPattern(FULL_DATE_PATTERN, Locale.getDefault())

    return localDate.format(formatter)
}