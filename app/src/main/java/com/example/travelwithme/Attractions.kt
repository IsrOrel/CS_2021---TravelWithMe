package com.example.travelwithme

import Attraction_Adapter
import com.example.travelwithme.CategoryAdapter
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
import com.example.travelwithme.Data.SelectedAttraction
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

        setupCategoryRecyclerView()
        setupAttractionRecyclerView()

        categoryAdapter.onCategoryClick = { category ->
            if (category.description == "כללי") {
                loadAllAttractionsForCity(cityName)
            } else {
                loadAttractionsForCategory(category)
            }
        }

        attractionAdapter.onImageClick = { attraction ->
            showDateTimePopup(attraction)
        }

        binding.goBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_attractions_to_home_screen)
        }

        // Load city name asynchronously
        val currentUserEmail = UserSession.getCurrentUserEmail()
        cityName = arguments?.getString("cityName") ?: "לונדון"
        if (currentUserEmail != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val destination = userDao.getDestination(currentUserEmail)
                    withContext(Dispatchers.Main) {
                        cityName = destination ?: cityName
                        Log.d("AttractionsFragment", "City from user: $cityName")

                        loadCategoriesForDestination(cityName)
                        loadAllAttractionsForCity(cityName)
                    }
                } catch (e: Exception) {
                    Log.e("AttractionsFragment", "Error fetching destination: ${e.message}")
                }
            }
        } else {
            Log.d("AttractionsFragment", "City selected: $cityName")
            loadCategoriesForDestination(cityName)
            loadAllAttractionsForCity(cityName)
        }
    }

    private fun loadAllAttractionsForCity(city: String) {
        attractionViewModel.getAttractionsForCity(city).observe(viewLifecycleOwner) { attractions ->
            val uniqueAttractions = attractions.distinctBy { it.title }
            Log.d("Attractions", "Loaded ${uniqueAttractions.size} unique attractions for $city")
            attractionAdapter.updateAttractions(uniqueAttractions)
        }
    }

    private fun setupCategoryRecyclerView() {
        binding.categoriesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
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
            "לונדון", "רומא", "אמסטרדם" -> listOf(
                Category(CategoryIcons.getIconForCategory("All"), "כללי"),
                Category(CategoryIcons.getIconForCategory("Beach"), "חופים"),
                Category(CategoryIcons.getIconForCategory("Museum"), "מוזיאונים"),
                Category(CategoryIcons.getIconForCategory("Park"), "פארקים"),
                Category(CategoryIcons.getIconForCategory("Shopping"), "קניות"),
                Category(CategoryIcons.getIconForCategory("Night Life"), "חיי לילה"),
                Category(CategoryIcons.getIconForCategory("Restaurant"), "מסעדות")

            )
            else -> listOf(Category(CategoryIcons.getIconForCategory("All"), "כללי"))
        }
        Log.d("AttractionsFragment", "Categories to update: $categories")
        categoryAdapter.updateCategories(categories)
    }

    private fun loadAttractionsForCategory(category: Category) {
        attractionViewModel.getAttractionsForCityAndCategory(cityName, category.description)
            .observe(viewLifecycleOwner) { attractions ->
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

    private fun addEventToCalendar(
        attraction: Attraction_Data,
        date: Date,
        durationHours: Int,
        category: String
    ) {
        val currentUserEmail = UserSession.getCurrentUserEmail()
        if (currentUserEmail != null) {
            val selectedAttraction = SelectedAttraction(
                title = attraction.title,
                plannedDate = date,
                plannedTime = calculateTimeRange(date, durationHours),
                category = category
            )

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    userDao.addSelectedAttraction(currentUserEmail, selectedAttraction)
                    withContext(Dispatchers.Main) {
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
                            "Added ${attraction.title} to calendar on ${
                                SimpleDateFormat(
                                    "dd MMM yyyy",
                                    Locale.getDefault()
                                ).format(date)
                            } $timeRange",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e("AttractionsFragment", "Error adding event to calendar: ${e.message}")
                }
            }
        } else {
            Log.e("AttractionsFragment", "Current user email is null")
        }
    }

    private fun calculateTimeRange(startDate: Date, durationHours: Int): String {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance().apply { time = startDate }
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
