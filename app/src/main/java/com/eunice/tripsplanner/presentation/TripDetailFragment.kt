package com.eunice.tripsplanner.presentation

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.eunice.tripsplanner.R
import com.eunice.tripsplanner.databinding.FragmentTripDetailBinding
import com.eunice.tripsplanner.databinding.LayoutTripActivitiesBinding
import com.eunice.tripsplanner.databinding.LayoutTripFlightsBinding
import com.eunice.tripsplanner.databinding.LayoutTripHotelsBinding
import com.eunice.tripsplanner.network.model.Activity
import com.eunice.tripsplanner.network.model.Flight
import com.eunice.tripsplanner.network.model.Hotel
import com.eunice.tripsplanner.network.model.Trip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

const val FLIGHT_DATE_PATTERN = "E, dd MMM"

class TripDetailFragment : Fragment(R.layout.fragment_trip_detail) {
    val binding by viewBinding(FragmentTripDetailBinding::bind)
    val viewModel by activityViewModels<TripsHomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.getTrip()?.let { setupView(it) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.flightsUiState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                if (it.success.isNotEmpty()) {
                    updateFlightsView(it.success.first())
                }
            }

            viewModel.hotelsUiState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                if (it.success.isNotEmpty()) {
                    updateHotelsView(it.success.first())
                }
            }

            viewModel.activitiesUiState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                if (it.success.isNotEmpty()) {
                    updateActivitiesView(it.success.first())
                }
            }
        }

        setupClickListeners()
    }



    fun setupFlightView(flightsBinding: LayoutTripFlightsBinding, flight: Flight) {
        flightsBinding.tvFlightName.text = flight.name
        flightsBinding.tvFlightCode.text = flight.code
        flightsBinding.tvFlightPrice.text = flight.price
        flightsBinding.tvFlightStartDay.text = getDateAsFlightString(flight.outboundDate)
        flightsBinding.tvFlightEndDay.text = getDateAsFlightString(flight.outboundDate)
        flightsBinding.tvFlightStartTime.text = flight.arrivalTime
        flightsBinding.tvFlightEndTime.text = flight.departureTime
        flightsBinding.tvRemoveFlight.setOnClickListener {
            removeView(
                binding.llNewFlight,
                binding.llFlightsDetail,
                flightsBinding.root)
        }
    }

    fun setupHotelsView(hotelsBinding: LayoutTripHotelsBinding, hotel: Hotel) {
        hotelsBinding.tvHotelName.text = hotel.name
        hotelsBinding.tvHotelAddress.text = hotel.address
        hotelsBinding.tvHotelRating.text = "${hotel.rating}(${hotel.noOfRatings})"
        hotelsBinding.tvHotelType.text = hotel.roomType
        hotelsBinding.tvHotelPrice.text = hotel.pricePerNight
        hotelsBinding.tvRemoveHotel.setOnClickListener {
            removeView(
                binding.llNewHotel,
                binding.llHotelsDetail,
                hotelsBinding.root
            )
        }
    }

    private fun setupActivitiesView(activitiesBinding: LayoutTripActivitiesBinding, activity: Activity) {
        activitiesBinding.tvActivityName.text = activity.name
        activitiesBinding.tvActivityDesc.text = activity.description
        activitiesBinding.tvActivityPrice.text = activity.price
        activitiesBinding.tvActivityTime.text = activity.datetime
        activitiesBinding.tvRemoveActivity.setOnClickListener {
            removeView(
                binding.llNewActivity,
                binding.llActivities,
                activitiesBinding.root
            )
        }
    }


    private fun addNewView(parent: LinearLayout, remove: LinearLayout, replacement: ViewGroup) {
        remove.visibility = View.GONE
        parent.addView(replacement)
    }

    private fun removeView(parent: LinearLayout, remove: LinearLayout, replacement: ViewGroup) {
        remove.isVisible = true
        parent.removeView(replacement)
    }

    private fun updateFlightsView(flight: Flight?) {
        val flightsView = LayoutInflater.from(context).inflate(R.layout.layout_trip_flights, null)
        val flightsBinding = LayoutTripFlightsBinding.bind(flightsView)
        flight?.let { setupFlightView(flightsBinding, it) }

        addNewView(
            binding.llNewFlight,
            binding.llFlightsDetail,
            flightsBinding.root
        )
    }

    private fun updateHotelsView(hotel: Hotel?) {
        val hotelsView = LayoutInflater.from(context).inflate(R.layout.layout_trip_hotels, null)
        val hotelsBinding = LayoutTripHotelsBinding.bind(hotelsView)
        hotel?.let { setupHotelsView(hotelsBinding, it) }

        addNewView(
            binding.llNewHotel,
            binding.llHotelsDetail,
            hotelsBinding.root
        )
    }

    private fun updateActivitiesView(activity: Activity?) {
        val activitiesView = LayoutInflater.from(context).inflate(R.layout.layout_trip_activities, null)
        val activitiesBinding = LayoutTripActivitiesBinding.bind(activitiesView)

        activity?.let { setupActivitiesView(activitiesBinding, it) }

        addNewView(
            binding.llNewActivity,
            binding.llActivitiesDetail,
            activitiesBinding.root
        )
    }

    private fun setupView(trip: Trip) = with(binding) {
        tvTripName.text = trip.name
        tvStartDate.text = trip.startDate
        tvEndDate.text = trip.endDate

    }

    private fun setupClickListeners() = with(binding) {
        tvAddFlights.setOnClickListener {
            cvFlights.parent.requestChildFocus(cvFlights, cvFlights)
        }

        tvAddHotels.setOnClickListener {
            cvHotels.parent.requestChildFocus(cvHotels, cvHotels)
        }

        tvAddActivities.setOnClickListener {
            cvActivities.parent.requestChildFocus(cvActivities, cvActivities)
        }

        btnAddFlights.setOnClickListener {
            viewModel.getFlights()
        }

        btnAddHotels.setOnClickListener {
            viewModel.getHotels()
        }

        btnAddActivities.setOnClickListener {
            viewModel.getActivities()
        }
    }

    private fun getDateAsFlightString(date: String): String {
        val localDate = LocalDate.parse(date,
            DateTimeFormatter.ofPattern(FULL_DATE_PATTERN))

        val formatter = DateTimeFormatter.ofPattern(FLIGHT_DATE_PATTERN, Locale.getDefault())

        return localDate.format(formatter)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TripDetailFragment().apply {

            }
    }
}