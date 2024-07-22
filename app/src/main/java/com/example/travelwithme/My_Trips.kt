package com.example.travelwithme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.travelwithme.databinding.MyTripsBinding
import com.example.travelwithme.Data.TravelDatabase
import com.example.travelwithme.Data.User_Data
import com.example.travelwithme.Data.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.*

class My_Trips : Fragment() {
    private var _binding: MyTripsBinding? = null
    private val binding get() = _binding!!
    private var currentTrip: User_Data? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        loadTrip()
    }

    private fun setupUI() {
        binding.ThisTrip.setOnClickListener {
            findNavController().navigate(R.id.action_my_Trips_to_home_screen)
        }

        binding.AddTrip.setOnClickListener {
            findNavController().navigate(R.id.action_my_Trips_to_add_flight)
        }

        binding.tripCardView.setOnClickListener {
            findNavController().navigate(R.id.action_my_Trips_to_home_screen)
        }

        binding.deleteTrip.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Trip")
            .setMessage("Are you sure you want to delete this trip?")
            .setPositiveButton("Yes") { _, _ ->
                deleteTrip()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteTrip() {
        GlobalScope.launch(Dispatchers.IO) {
            val userEmail = UserSession.getCurrentUserEmail()
            if (userEmail != null) {
                val userDao = TravelDatabase.getInstance(requireContext()).userDao()

                // Reset trip details
                userDao.updateDestination(userEmail, "")
                userDao.updateTripDates(userEmail, 0, 0)

                // Clear other related data
                userDao.updateAttractions(userEmail, emptyList())
                userDao.updateHotels(userEmail, emptyList())
                userDao.updateChecklist(userEmail, emptyList())

                withContext(Dispatchers.Main) {
                    hideTripDetails()
                    loadTrip() // Reload to reflect changes
                }
            }
        }
    }

    private fun loadTrip() {
        GlobalScope.launch(Dispatchers.IO) {
            val userEmail = UserSession.getCurrentUserEmail()
            if (userEmail != null) {
                val userDao = TravelDatabase.getInstance(requireContext()).userDao()
                currentTrip = userDao.getUserByEmail(userEmail)

                withContext(Dispatchers.Main) {
                    if (currentTrip != null && currentTrip?.destination?.isNotEmpty() == true) {
                        showTripDetails()
                    } else {
                        hideTripDetails()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    hideTripDetails()
                }
            }
        }
    }

    private fun showTripDetails() {
        binding.tripCardView.visibility = View.VISIBLE
        binding.ThisTrip.visibility = View.VISIBLE
        binding.AddTrip.isEnabled = false

        // Populate trip details
        binding.destinationTextView.text = currentTrip?.destination
        binding.takeOffDateTextView.text = "Take-off: ${formatDate(currentTrip?.takeOffDate)}"
        binding.landingDateTextView.text = "Landing: ${formatDate(currentTrip?.landingDate)}"
    }

    private fun formatDate(date: Date?): String {
        return if (date != null) {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatter.format(date)
        } else {
            "Not set"
        }
    }

    private fun hideTripDetails() {
        binding.tripCardView.visibility = View.GONE
        binding.ThisTrip.visibility = View.GONE
        binding.AddTrip.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}