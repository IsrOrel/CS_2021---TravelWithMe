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
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwithme.Data.AttractionViewModel
import com.example.travelwithme.Data.Attraction_Data
import com.example.travelwithme.databinding.AttractionsBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import java.text.SimpleDateFormat
import java.util.*

class Attractions : Fragment() {
    private var _binding: AttractionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var attractionAdapter: Attraction_Adapter
    private val attractionViewModel: AttractionViewModel by viewModels()
    private lateinit var cityName: String

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

        // Get the chosen city from arguments
        cityName = arguments?.getString("cityName") ?: "Default City"
        Log.d("AttractionsFragment", "City selected: $cityName")


        // Initialize RecyclerViews
        setupCategoryRecyclerView()
        setupAttractionRecyclerView()

        // Load categories for the chosen city
        loadCategoriesForCity(cityName)

        // Load all attractions for the chosen city
        loadAllAttractionsForCity(cityName)

        // Set up category click listener
        categoryAdapter.onCategoryClick = { category ->
            if (category.description == "All") {
                loadAllAttractionsForCity(cityName)
            } else {
                loadAttractionsForCategory(category)
            }
        }

        // Set up attraction image click listener
        attractionAdapter.onImageClick = { attraction ->
            showDateTimePopup(attraction)
        }

        binding.goBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_attractions_to_home_screen)
        }
    }


    private fun loadAllAttractionsForCity(city: String) {
        attractionViewModel.getAllAttractionsForCity(city).observe(viewLifecycleOwner) { attractions ->
            attractionAdapter.updateAttractions(attractions)
        }
    }

    private fun setupCategoryRecyclerView() {
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter(emptyList())
        binding.categoriesRecyclerView.adapter = categoryAdapter
    }

    private fun setupAttractionRecyclerView() {
        binding.attractionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        attractionAdapter = Attraction_Adapter(emptyList())
        binding.attractionsRecyclerView.adapter = attractionAdapter
    }

    private fun loadCategoriesForCity(city: String) {
        // This should ideally come from a database or API
        val categories = when (city) {
            "London", "Rome", "Amsterdam" -> listOf(
                Category(R.drawable.icon_all, "All"),
                Category(R.drawable.icon_restaurant, "Restaurants"),
                Category(R.drawable.icon_park, "Parks"),
                Category(R.drawable.icon_museum, "Museums"),
                Category(R.drawable.icon_shopping, "Shopping"),
                Category(R.drawable.icon_nightlife, "Night Life")
            )
            else -> listOf(Category(R.drawable.icon_all, "All"))
        }

        categoryAdapter.updateCategories(categories)
    }

    private fun loadAttractionsForCategory(category: Category) {
        attractionViewModel.getAttractionsForCityAndCategory(cityName, category.description).observe(viewLifecycleOwner) { attractions ->
            attractionAdapter.updateAttractions(attractions)
        }
    }

    private fun showDateTimePopup(attraction: Attraction_Data) {
        val popupFragment = PopupFragment()
        popupFragment.listener = object : PopupFragment.OnDateTimeSelectedListener {
            override fun onDateTimeSelected(date: Date, durationHours: Int) {
                addEventToCalendar(attraction, date, durationHours, attraction.category)
            }
        }
        popupFragment.show(parentFragmentManager, "PopupFragment")
    }

    private fun addEventToCalendar(attraction: Attraction_Data, date: Date, durationHours: Int, category: String) {
        val action = AttractionsDirections.actionAttractionsToCalendar(
            attraction.title,
            date.time,
            durationHours,
            category
        )

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
