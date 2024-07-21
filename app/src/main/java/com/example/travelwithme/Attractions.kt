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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwithme.Data.AttractionViewModel
import com.example.travelwithme.Data.Attraction_Data
import com.example.travelwithme.databinding.AttractionsBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelwithme.Data.TravelDatabase
import com.example.travelwithme.Data.UserSession
import com.example.travelwithme.Data.User_Dao
import java.text.SimpleDateFormat
import java.util.*

class Attractions : Fragment() {
    private var _binding: AttractionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var userDao: User_Dao
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
        userDao = TravelDatabase.getInstance(requireContext()).userDao()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerViews
        setupCategoryRecyclerView()
        setupAttractionRecyclerView()

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

        // Load city name asynchronously
        val currentUserEmail = UserSession.getCurrentUserEmail()
        cityName = arguments?.getString("cityName") ?: "London"
        if (currentUserEmail != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                // Fetch destination from the database
                val destination = userDao.getDestination(currentUserEmail)

                // Switch to Main Thread to update UI
                withContext(Dispatchers.Main) {
                    cityName = destination ?: cityName
                    Log.d("AttractionsFragment", "City from user: $cityName")

                    // Reload categories and attractions with the updated city name
                    loadCategoriesForDestination(cityName)
                    loadAllAttractionsForCity(cityName)
                }
            }
        } else {
            Log.d("AttractionsFragment", "City selected: $cityName")
            // Load categories and attractions
            loadCategoriesForDestination(cityName)
            loadAllAttractionsForCity(cityName)
        }
    }

    private fun loadAllAttractionsForCity(city: String) {
        attractionViewModel.getAttractionsForCity(city).observe(viewLifecycleOwner) { attractions ->
            Log.d("Attractions", "Loaded ${attractions.size} attractions for $city")
            val uniqueAttractions = attractions.distinctBy { it.title }
            Log.d("Attractions", "Unique attractions: ${uniqueAttractions.size}")
            uniqueAttractions.forEach { attraction ->
                Log.d("Attractions", "Attraction: ${attraction.title}, ID: ${attraction.title}")
            }
            attractionAdapter.updateAttractions(uniqueAttractions)
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

    private fun loadCategoriesForDestination(city: String) {
        val categories = when (city) {
            "London", "Rome", "Amsterdam" -> listOf(
                Category(CategoryIcons.getIconForCategory("All"), "All"),
                Category(CategoryIcons.getIconForCategory("Restaurant"), "Restaurant"),
                Category(CategoryIcons.getIconForCategory("Park"), "Park"),
                Category(CategoryIcons.getIconForCategory("Museum"), "Museum"),
                Category(CategoryIcons.getIconForCategory("Shopping"), "Shopping"),
                Category(CategoryIcons.getIconForCategory("Night Life"), "Night Life"),
                Category(CategoryIcons.getIconForCategory("Beach"), "Beach")
            )
            else -> listOf(Category(CategoryIcons.getIconForCategory("All"), "All"))
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
