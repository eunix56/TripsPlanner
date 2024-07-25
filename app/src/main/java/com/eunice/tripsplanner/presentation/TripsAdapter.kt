package com.eunice.tripsplanner.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eunice.tripsplanner.databinding.ItemTripCardBinding
import com.eunice.tripsplanner.network.model.Trip
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * Created by {EUNICE BAKARE T.} on {7/25/24}
 * Email: {eunice@reach.africa}
 */

const val FULL_DATE_PATTERN = "yyyy-MM-dd"

class TripsAdapter: ListAdapter<Trip, TripsAdapter.TripsViewHolder>(DIFF_UTIL) {

    class TripsViewHolder(private val itemTripCardBinding: ItemTripCardBinding)
        : RecyclerView.ViewHolder(itemTripCardBinding.root) {

            fun bind(trip: Trip) = with(itemTripCardBinding) {
                val days = TimeUnit.MILLISECONDS.toDays(
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsViewHolder {
        return TripsViewHolder(
            ItemTripCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TripsViewHolder, position: Int) {
       holder.bind(getItem(position))
    }
}


fun getLongDateTime(date: String): Long {
    return LocalDate.parse(date,
        DateTimeFormatter.ofPattern(FULL_DATE_PATTERN))
        .atStartOfDay()
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}
