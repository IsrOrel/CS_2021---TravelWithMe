package com.example.travelwithme

import Attraction_Adapter
import CategoryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwithme.Data.AttractionViewModel
import com.example.travelwithme.Data.Attraction_Data
import com.example.travelwithme.databinding.AttractionsBinding

class Attractions : Fragment() {
    private var _binding: AttractionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter_category: CategoryAdapter
    private lateinit var adapter_attraction: Attraction_Adapter


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

        val categories = listOf(
            Category(R.drawable.icon_restaurant, "Restaurants"),
            Category(R.drawable.icon_museum, "Museums"),
            Category(R.drawable.icon_park, "Parks"),
            Category(R.drawable.icon_shopping, "Shopping"),
            Category(R.drawable.icon_nightlife, "Nightlife"),
            Category(R.drawable.icon_beach, "Beaches")
        )

        val attractions = listOf(
            Attraction_Data(id = 0, image = R.drawable.icon_restaurant, title = "Restaurant 1", description = "Great food place", place = "City 1"),
            Attraction_Data(id = 0, image = R.drawable.icon_museum, title = "Museum 1", description = "Historical artifacts", place = "City 2"),
            Attraction_Data(id = 0, image = R.drawable.icon_park, title = "Park 1", description = "Beautiful park", place = "City 3"),
            Attraction_Data(id = 0, image = R.drawable.icon_shopping, title = "Shopping 1", description = "Shopping mall", place = "City 4"),
            Attraction_Data(id = 0, image = R.drawable.icon_nightlife, title = "Nightlife 1", description = "Night clubs", place = "City 5"),
            Attraction_Data(id = 0, image = R.drawable.icon_beach, title = "Beach 1", description = "Sunny beach", place = "City 6")
        )


        setupCategoryRecyclerView(categories)
        setupAttractionRecyclerView(attractions)

        // Insert attractions data
        attractionViewModel.insertAttractions(attractions)

        // Observe the live data
        attractionViewModel.attractions.observe(viewLifecycleOwner, { attractions ->
            // Update your UI with the fetched data
            adapter_attraction = Attraction_Adapter(attractions)
            binding.attractionsRecyclerView.adapter = adapter_attraction
        })
    }

    private fun setupCategoryRecyclerView(categories: List<Category>) {
        val recyclerView = binding.categoriesRecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1, RecyclerView.HORIZONTAL, false)
        adapter_category = CategoryAdapter(categories)
        recyclerView.adapter = adapter_category
    }

    private fun setupAttractionRecyclerView(attractions: List<Attraction_Data>) {
        val recyclerView = binding.attractionsRecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1, RecyclerView.VERTICAL, false)
        adapter_attraction = Attraction_Adapter(attractions)
        recyclerView.adapter = adapter_attraction
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
