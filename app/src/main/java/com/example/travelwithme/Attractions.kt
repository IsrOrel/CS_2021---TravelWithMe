package com.example.travelwithme

import Attraction_Adapter
import CategoryAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwithme.Data.AttractionViewModel
import com.example.travelwithme.Data.Attraction_Data
import com.example.travelwithme.databinding.AttractionsBinding
import com.example.travelwithme.AttractionsDirections
import androidx.navigation.fragment.findNavController
import java.text.SimpleDateFormat
import java.util.*

class Attractions : Fragment() {
    private var _binding: AttractionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterCategory: CategoryAdapter
    private lateinit var adapterAttraction: Attraction_Adapter


    private val attractionViewModel: AttractionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AttractionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the go back button
        binding.goBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Initialize RecyclerViews
        setupCategoryRecyclerView()
        setupAttractionRecyclerView()

        // Example data
        val categories = listOf(
            Category(R.drawable.icon_restaurant, "Restaurants"),
            // Add other categories here...
        )

        val attractions = listOf(
            Attraction_Data(id = 0, image = R.drawable.icon_restaurant, title = "Restaurant 1", description = "Great food place", place = "City 1"),
            // Add other attractions here...
        )

        // Insert attractions data into ViewModel
        attractionViewModel.insertAttractions(attractions)

        // Observe attraction data from ViewModel
        attractionViewModel.attractions.observe(viewLifecycleOwner) { attractions ->
            adapterAttraction.updateAttractions(attractions)
        }

        // Handle attraction image clicks
        adapterAttraction.onImageClick = { attraction ->
            showDateTimePopup(attraction)
        }
    }

    private fun showDateTimePopup(attraction: Attraction_Data) {
        val popupFragment = PopupFragment()
        popupFragment.listener = object : PopupFragment.OnDateTimeSelectedListener {
            override fun onDateTimeSelected(date: Date, durationHours: Int) {
                addEventToCalendar(attraction, date, durationHours)
            }
        }
        popupFragment.show(parentFragmentManager, "PopupFragment")
    }

    private fun addEventToCalendar(attraction: Attraction_Data, date: Date, durationHours: Int) {
        val action = AttractionsDirections.actionAttractionsToCalendar(attraction.title, date.time, durationHours)
        findNavController().navigate(action)

        val timeRange = calculateTimeRange(date, durationHours)
        Toast.makeText(
            requireContext(),
            "Added ${attraction.title} to calendar on ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)} $timeRange",
            Toast.LENGTH_SHORT
        ).show()
    }
    private fun calculateTimeRange(startDate: Date, durationHours: Int): String {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        val startTime = formatter.format(calendar.time)
        calendar.add(Calendar.HOUR_OF_DAY, durationHours)
        val endTime = formatter.format(calendar.time)
        return "$startTime-$endTime"
    }

    private fun setupCategoryRecyclerView() {
        binding.categoriesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1, RecyclerView.HORIZONTAL, false)
        adapterCategory = CategoryAdapter(emptyList())
        binding.categoriesRecyclerView.adapter = adapterCategory
    }

    private fun setupAttractionRecyclerView() {
        binding.attractionsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1, RecyclerView.VERTICAL, false)
        adapterAttraction = Attraction_Adapter(emptyList())
        binding.attractionsRecyclerView.adapter = adapterAttraction
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
