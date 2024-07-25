package com.eunice.tripsplanner.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.eunice.tripsplanner.R
import com.eunice.tripsplanner.databinding.FragmentWhereTripsBinding
import com.eunice.tripsplanner.network.model.City
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val MIN_CHAR = 3

class WhereTripsFragment : Fragment(R.layout.fragment_where_trips) {
    val binding by viewBinding(FragmentWhereTripsBinding::bind)
    var itemSelected = false
    val viewModel by activityViewModels<TripsHomeViewModel>()
    var adapter: ArrayAdapter<String>? = null
    val handler = Handler(Looper.getMainLooper())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCities().flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                if (it.data != null) {
                    setupAdapter(it.data.map { it.name })
                    if (!itemSelected) {
                        binding.actvCities.showDropDown()
                    }
                }
            }
        }

        setupAutoCompleteDropDown()
    }

    private fun setupAdapter(list: List<String>) = with(binding) {
        adapter = ArrayAdapter(
            requireContext(),
            R.layout.textview_layout,
            list
        )
        actvCities.setOnClickListener { actvCities.showDropDown() }
        actvCities.setOnFocusChangeListener { _, b ->
            if (b) actvCities.showDropDown()
        }
        actvCities.setAdapter(adapter)
    }


    private fun setupAutoCompleteDropDown() = with(binding) {
        actvCities.setOnItemClickListener { _, _, i, _ ->
            itemSelected = true
            viewModel.setCity(adapter?.getItem(i))
        }

        actvCities.threshold = 2
        actvCities.doOnTextChanged { s: CharSequence?, _, _, _ ->
            val keyword = s.toString()

            if (keyword.length > MIN_CHAR) {
                handler.postDelayed( Runnable {
                    if (itemSelected) {
                        itemSelected = false
                        return@Runnable
                    }
                    viewModel.setName(keyword)
                }, 300)
            }
        }
    }



    companion object {
        @JvmStatic
        fun newInstance() =
            WhereTripsFragment().apply {

            }
    }
}