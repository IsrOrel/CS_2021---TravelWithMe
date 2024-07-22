package com.example.travelwithme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelwithme.Data.TravelDatabase
import com.example.travelwithme.Data.UserSession
import com.example.travelwithme.databinding.MyHotelsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class My_hotels : Fragment() {
    private var _binding: MyHotelsBinding? = null
    private val binding get() = _binding!!
    private lateinit var hotelAdapter: HotelAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyHotelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadHotels()

        val fromHomeScreen = arguments?.getBoolean("fromHomeScreen") ?: false

        if (fromHomeScreen) {
            binding.Return.isEnabled = false
            binding.Skip.isEnabled = false
        }

        binding.Done.setOnClickListener {
            findNavController().navigate(R.id.action_add_Hotel_to_home_screen)
        }
        binding.Skip.setOnClickListener {
            findNavController().navigate(R.id.action_add_Hotel_to_home_screen)
        }
        binding.Return.setOnClickListener {
            findNavController().navigate(R.id.action_add_Hotel_to_add_flight)
        }
        binding.AddHotel.setOnClickListener {
            findNavController().navigate(R.id.action_add_Hotel_to_add_Edit_Hotel)
        }
    }

    private fun setupRecyclerView() {
        hotelAdapter = HotelAdapter { hotel ->
            // Handle hotel item click (e.g., navigate to edit screen)
            // For now, we'll just navigate to the Add/Edit screen without passing data
            findNavController().navigate(R.id.action_add_Hotel_to_add_Edit_Hotel)
        }
        binding.hotelsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = hotelAdapter
        }
    }

    private fun loadHotels() {
        val userEmail = UserSession.getCurrentUserEmail()
        if (userEmail != null) {
            val userDao = TravelDatabase.getInstance(requireContext()).userDao()
            lifecycleScope.launch(Dispatchers.IO) {
                val user = userDao.getUserByEmail(userEmail)
                withContext(Dispatchers.Main) {
                    user?.let {
                        if (it.hotels.isEmpty()) {
                            binding.emptyStateTextView.visibility = View.VISIBLE
                            binding.hotelsRecyclerView.visibility = View.GONE
                        } else {
                            binding.emptyStateTextView.visibility = View.GONE
                            binding.hotelsRecyclerView.visibility = View.VISIBLE
                            hotelAdapter.submitList(it.hotels)
                        }
                    }
                }
            }
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}