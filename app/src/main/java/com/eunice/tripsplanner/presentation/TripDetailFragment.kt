package com.eunice.tripsplanner.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.eunice.tripsplanner.R
import com.eunice.tripsplanner.databinding.FragmentTripDetailBinding
import com.eunice.tripsplanner.network.model.Trip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class TripDetailFragment : Fragment(R.layout.fragment_trip_detail) {
    val binding by viewBinding(FragmentTripDetailBinding::bind)
    val viewModel by viewModels<TripsHomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.flightsUiState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                if (it.success.isNotEmpty()) {

                }
            }
        }
    }

    fun replaceViews() {

    }

    fun setupView(trip: Trip) = with(binding) {
        tvTripName.text = trip.name
        tvStartDate.text = trip.startDate
        tvEndDate.text = trip.endDate

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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TripDetailFragment().apply {

            }
    }
}