package com.eunice.tripsplanner.presentation

import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.eunice.tripsplanner.R
import com.eunice.tripsplanner.databinding.FragmentTripsHomeBinding
import com.eunice.tripsplanner.presentation.MainActivity.Companion.startFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


class TripsHomeFragment : Fragment(R.layout.fragment_trips_home) {
    private val binding by viewBinding(FragmentTripsHomeBinding::bind)
    val viewModel by activityViewModels<TripsHomeViewModel>()
    private lateinit var createTripBehavior: BottomSheetBehavior<ConstraintLayout>
    private var selectedState = BottomSheetBehavior.STATE_HIDDEN
    val adapter = TripsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getTrips()
        setupTravelStyle()
        setupCreateTrip()
        setupCreateTripBehaviour()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tripsUiState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                if (it.loading) Toast.makeText(context, "Loading...", Toast.LENGTH_LONG).show()

                else if (it.success.isNotEmpty()) {
                    Toast.makeText(context, "Successful!", Toast.LENGTH_LONG).show()
                    adapter.submitList(it.success)
                    binding.recyclerTrips.adapter = adapter
                } else if (it.error != null) {
                    Toast.makeText(context, it.error, Toast.LENGTH_LONG).show()
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.postTripsUiState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                if (it.loading) Toast.makeText(context, "Loading...", Toast.LENGTH_LONG).show()
                else if (it.success != null) {
                    Toast.makeText(context, "Successful!", Toast.LENGTH_LONG).show()
                    viewModel.setTrip(it.success)
                    requireActivity().startFragment(TripDetailFragment.newInstance(), true)
                } else if (it.error != null) {
                    Toast.makeText(context, it.error, Toast.LENGTH_LONG).show()
                }
            }
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
            )
    }

    private fun setupTravelStyle() = with(binding.createTripSheet) {
        val context = ContextThemeWrapper(context , R.style.TravelStyle)
        val popupMenu = PopupMenu(context, tvTravelStyle)
        popupMenu.menuInflater.inflate(R.menu.menu_travel_style, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            item -> tvPickStyle.text = item?.title
            return@OnMenuItemClickListener true
        })

        tvPickStyle.setOnClickListener {
            popupMenu.show()
        }

        btnCreateTrip.isEnabled = validate()

        btnCreateTrip.setOnClickListener {
            viewModel.cityName?.let { city ->
                viewModel.postTrip(
                    tvTripName.text.toString(),
                    tvTravelStyle.text.toString(),
                    city,
                    viewModel.getTripStartDate(),
                    viewModel.getTripEndDate()
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
            .build()

        materialDatePicker.addOnPositiveButtonClickListener {
            viewModel.startDate = getDateAsString(it.first)
            viewModel.endDate = getDateAsString(it.second)
            tvStartDateVal.text = viewModel.startDate
            tvEndDateVal.text = viewModel.endDate
        }

        if (viewModel.getCity().isNullOrEmpty().not()) {
            tvCity.text = viewModel.getCity()
        }

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

        btnCreateATrip.setOnClickListener {
           showCreateTrip()
        }
    }

    private fun setupCreateTripBehaviour() = with(binding) {
        createTripBehavior = BottomSheetBehavior.from(createTripSheet.clCreateTrip)
        createTripBehavior.isHideable = true
        createTripBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        createTripBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> showCreateTrip()
                    BottomSheetBehavior.STATE_HIDDEN -> darkOverlay.isVisible = false
                    BottomSheetBehavior.STATE_COLLAPSED -> darkOverlay.isVisible = false
                    else -> {}
                }
                createTripBehavior.state =
                    if (newState == BottomSheetBehavior.STATE_SETTLING
                        || newState == BottomSheetBehavior.STATE_DRAGGING)
                        selectedState
                    else newState
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        darkOverlay.setOnClickListener {
            selectedState = BottomSheetBehavior.STATE_HIDDEN
            createTripBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        createTripSheet.ivClose.setOnClickListener {
            selectedState = BottomSheetBehavior.STATE_HIDDEN
            createTripBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showCreateTrip() = with(binding) {
        darkOverlay.isVisible = true
        selectedState = BottomSheetBehavior.STATE_EXPANDED
        createTripBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TripsHomeFragment().apply {

            }
        fun getDateAsString(timeInMillis: Long): String {
            val localDate = Instant.ofEpochMilli(timeInMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            val formatter = DateTimeFormatter.ofPattern(FULL_DATE_PATTERN, Locale.getDefault())

            return localDate.format(formatter)
        }
    }
}