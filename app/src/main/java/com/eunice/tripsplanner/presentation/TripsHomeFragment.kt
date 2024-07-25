package com.eunice.tripsplanner.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.eunice.tripsplanner.R
import com.eunice.tripsplanner.databinding.FragmentTripsHomeBinding
import com.eunice.tripsplanner.presentation.MainActivity.Companion.startFragment
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class TripsHomeFragment : Fragment(R.layout.fragment_trips_home) {
    private val binding by viewBinding(FragmentTripsHomeBinding::bind)
    val viewModel by activityViewModels<TripsHomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTravelStyle()
        setupCreateTrip()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getTrips().flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {

            }
        }
    }

    private fun setupTravelStyle() = with(binding.createTripSheet) {
        val popupMenu = PopupMenu(context, tvTravelStyle)
        popupMenu.menuInflater.inflate(R.menu.menu_travel_style, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            item ->
            tvTravelStyle.text = item?.title
            return@OnMenuItemClickListener true
        })

        btnCreateTrip.isEnabled = validate()

        btnCreateTrip.setOnClickListener {
            viewModel.city?.let { city ->
                viewModel.postTrip(
                    tvTripName.text.toString(),
                    tvTravelStyle.text.toString(),
                    city,
                    viewModel.startDate,
                    viewModel.endDate
                )
            }
        }
    }

    private fun validate(): Boolean = with(binding.createTripSheet) {
        return@with tvTripName.text.isNotEmpty() && tvTravelStyle.text.isNotEmpty()
            && tvTripDesc.text.isNotEmpty()
    }

    private fun setupCreateTrip() = with(binding) {
        val materialDatePicker = MaterialDatePicker
            .Builder
            .dateRangePicker()
            .setTheme(R.style.MaterialCalendarTheme)
            .build()

        rvCity.setOnClickListener {
            requireActivity().startFragment(
                WhereTripsFragment.newInstance(), true)
        }

        rvStartDate.setOnClickListener {
            materialDatePicker.show(childFragmentManager, "DATE_PICKER_RANGE")
        }

        rvEndDate.setOnClickListener {
            materialDatePicker.show(childFragmentManager, "DATE_PICKER_RANGE")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TripsHomeFragment().apply {

            }
    }
}