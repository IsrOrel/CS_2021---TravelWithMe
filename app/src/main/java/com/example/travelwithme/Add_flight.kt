package com.example.travelwithme

import SharedViewModel
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

    // Define these properties
    private var takeOffDate: Date? = null
    private var landingDate: Date? = null
    private var destination: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddFlightBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val calendarPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            val isGranted = it.value
            if (!isGranted) {
                Toast.makeText(context, "Calendar permissions are required to add events", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }
        }
        // Check if dates and destination are initialized
        if (takeOffDate != null && landingDate != null && destination != null) {
            addEventToCalendar(takeOffDate!!, landingDate!!, destination!!)
        } else {
            Toast.makeText(context, "Invalid flight details", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        db = TravelDatabase.getInstance(requireContext())
        userDao = db.userDao()

        // Initialize Spinner with city data
        setupCitySpinner()
        binding.return1.setOnClickListener{
            findNavController().navigate(R.id.action_add_flight_to_sign_In)
        }

        binding.doneBtn.setOnClickListener {
            destination = binding.citiesspinner.selectedItem.toString()
            val dateRange = binding.selectedDate.text.toString()
            val dates = dateRange.split(" - ")
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            takeOffDate = sdf.parse(dates[0])
            landingDate = sdf.parse(dates[1])

            if (takeOffDate != null && landingDate != null) {
                updateFlightDetails(takeOffDate!!, landingDate!!, destination!!)
                sharedViewModel.setCityName(destination!!) // Pass the city name to ViewModel

                // Check for calendar permissions
                if (hasCalendarPermissions()) {
                    addEventToCalendar(takeOffDate!!, landingDate!!, destination!!)
                } else {
                    requestCalendarPermissions()
                }
            } else {
                Toast.makeText(context, "Please select valid dates", Toast.LENGTH_SHORT).show()
            }

            findNavController().navigate(R.id.action_add_flight_to_add_Hotel)
        }

        selectedDate = binding.selectedDate
        datePicker = binding.datePicker
        datePicker.setOnClickListener { datePickerDialog() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Check and request location permissions
        checkLocationPermissions()
    }

    private fun hasCalendarPermissions(): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR)
        val writePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR)
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCalendarPermissions() {
        calendarPermissionRequest.launch(arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        ))
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
        // Get today's date in milliseconds
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        // Create a calendar constraint to disable past dates
        val calendarConstraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        // Build the date range picker
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("Select date range")
        builder.setSelection(androidx.core.util.Pair(today, today))
        builder.setCalendarConstraints(calendarConstraints)

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

    private fun addEventToCalendar(takeOffDate: Date, landingDate: Date, destination: String) {
        val calStart = Calendar.getInstance()
        calStart.time = takeOffDate
        val calEnd = Calendar.getInstance()
        calEnd.time = landingDate

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, calStart.timeInMillis)
            put(CalendarContract.Events.DTEND, calEnd.timeInMillis)
            put(CalendarContract.Events.TITLE, "Flight to $destination")
            put(CalendarContract.Events.DESCRIPTION, "Take off: $takeOffDate, Landing: $landingDate")
            put(CalendarContract.Events.CALENDAR_ID, 1)
            put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().timeZone.id)
        }

        val uri: Uri? = context?.contentResolver?.insert(CalendarContract.Events.CONTENT_URI, values)
        uri?.let {
            Toast.makeText(context, "Event added to calendar", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Failed to add event to calendar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
