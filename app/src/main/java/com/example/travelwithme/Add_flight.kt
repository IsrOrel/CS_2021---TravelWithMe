package com.example.travelwithme

import SharedViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.travelwithme.Data.TravelDatabase
import com.example.travelwithme.Data.UserSession
import com.example.travelwithme.Data.User_Dao
import com.example.travelwithme.databinding.AddFlightBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Add_flight : Fragment() {
    private lateinit var selectedDate: TextView
    private lateinit var datePicker: Button
    private var _binding: AddFlightBinding? = null
    private lateinit var userDao: User_Dao
    private lateinit var usersession: UserSession
    private lateinit var db: TravelDatabase
    private lateinit var sharedViewModel: SharedViewModel

    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddFlightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Initialize Spinner with city data
        setupCitySpinner()

        binding.doneBtn.setOnClickListener {
            val destination = binding.citiesspinner.selectedItem.toString()
            val dateRange = binding.selectedDate.text.toString()
            val dates = dateRange.split(" - ")
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val takeOffDate = sdf.parse(dates[0])
            val landingDate = sdf.parse(dates[1])

            if (takeOffDate != null && landingDate != null) {
                updateFlightDetails(takeOffDate, landingDate, destination)
                sharedViewModel.setCityName(destination) // Pass the city name to ViewModel
            } else {
                Toast.makeText(context, "Please select valid dates", Toast.LENGTH_SHORT).show()
            }

            findNavController().navigate(R.id.action_add_flight_to_home_screen)  // Navigate to home screen
        }

        selectedDate = binding.selectedDate
        datePicker = binding.datePicker
        datePicker.setOnClickListener { datePickerDialog() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Check and request location permissions
        checkLocationPermissions()
    }

    private fun setupCitySpinner() {
        val citiesArray = resources.getStringArray(R.array.cities)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, citiesArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.citiesspinner.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // Ensure permissions are checked again on resume
        checkLocationPermissions()
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permissions already granted, get last known location
            getLastKnownLocation()
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        // Update UI with location information
                        val address = getCityAndCountryFromLocation(it)
                        binding.textViewfrom.text = "From: $address"
                    }
                    // Handle case where location is null (e.g., if location services are disabled)
                    location ?: run {
                        binding.textViewfrom.text = "Location unavailable"
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure to get location
                    binding.textViewfrom.text = "Failed to get location: ${e.message}"
                }
        }
    }

    private fun getCityAndCountryFromLocation(location: Location): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        return if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            "${address.locality}, ${address.countryName}"
        } else {
            "Unknown location"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permissions granted, get last known location immediately
                getLastKnownLocation()
            } else {
                // Permission denied, handle accordingly
            }
        }
    }

    private fun datePickerDialog() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("Select date range")

        val dateRangePicker = builder.build()
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            if (selection != null) {
                val startDate = selection.first
                val endDate = selection.second
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val selectedDateRange = "${sdf.format(Date(startDate))} - ${sdf.format(Date(endDate))}"
                selectedDate.text = selectedDateRange
            }
        }

        dateRangePicker.show(parentFragmentManager, "dateRangePicker")
    }
    private fun updateFlightDetails(takeOffDate: Date, landingDate: Date, destination: String) {
        val currentUserEmail = UserSession.getCurrentUserEmail()
        if (currentUserEmail != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                userDao.updateTripDates(currentUserEmail, takeOffDate.time, landingDate.time)
                userDao.updateDestination(currentUserEmail,destination)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Flight details updated successfully", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
