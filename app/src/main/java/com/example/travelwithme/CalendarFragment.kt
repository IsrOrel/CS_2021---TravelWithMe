package com.example.travelwithme

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelwithme.Data.Event
import com.example.travelwithme.Data.SelectedAttraction
import com.example.travelwithme.Data.TravelDatabase
import com.example.travelwithme.Data.UserSession
import com.example.travelwithme.Data.User_Dao
import com.example.travelwithme.databinding.CalendarBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CalendarFragment : Fragment() {
    private var _binding: CalendarBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding should not be null")
    private lateinit var userDao: User_Dao
    private val events = mutableListOf<Event>()
    private lateinit var eventAdapter: EventAdapter
    private var takeOffDate: Calendar? = null
    private var landingDate: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CalendarBinding.inflate(inflater, container, false)
        userDao = TravelDatabase.getInstance(requireContext()).userDao()
        return binding.root
    }

    private val currentUserEmail = UserSession.getCurrentUserEmail()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchTravelDates()
        setupRecyclerView()
        fetchEvents()
        binding.goBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
        if (!::userDao.isInitialized) {
            Log.e("PopupFragment", "userDao has not been initialized")
            return
        }
    }

    private fun fetchTravelDates() {
        if (currentUserEmail != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val userData = userDao.getUserByEmail(currentUserEmail)
                withContext(Dispatchers.Main) {
                    takeOffDate = Calendar.getInstance().apply { time = userData?.takeOffDate ?: Date() }
                    landingDate = Calendar.getInstance().apply { time = userData?.landingDate ?: Date() }
                    setupCalendarView()
                }
            }
        }
    }

    private fun setupCalendarView() {
        try {
            takeOffDate?.let { takeOff ->
                landingDate?.let { landing ->
                    binding.calendarView.minDate = takeOff.timeInMillis
                    binding.calendarView.maxDate = landing.timeInMillis

                    binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                        val selectedDate = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }.time
                        showEventsForDate(selectedDate)
                    }
                } ?: run {
                    Toast.makeText(requireContext(), "Landing date is not set", Toast.LENGTH_LONG).show()
                }
            } ?: run {
                Toast.makeText(requireContext(), "Take-off date is not set", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("CalendarFragment", "Error setting up CalendarView: ${e.message}", e)
            Toast.makeText(requireContext(), "Error setting up calendar view.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(events)
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.eventsRecyclerView.adapter = eventAdapter
    }

    private fun fetchEvents() {
        if (currentUserEmail != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val selectedAttractions = userDao.getSelectedAttractions(currentUserEmail)
                withContext(Dispatchers.Main) {
                    updateEventList(selectedAttractions)
                }
            }
        }
    }

    private fun updateEventList(selectedAttractions: List<SelectedAttraction>) {
        events.clear()
        events.addAll(selectedAttractions.map { attraction ->
            Event(
                attraction = attraction,
                date = attraction.plannedDate,
                durationHours = calculateDurationHours(attraction.plannedTime)
            )
        })

        events.sortWith(compareBy<Event> { it.date }.thenBy { it.attraction.startTimeInt })
        showEventsForDate(Calendar.getInstance().time)
    }

    private fun calculateDurationHours(plannedTime: String): Int {
        val (start, end) = plannedTime.split("-")
        val startHour = start.split(":")[0].toInt()
        val endHour = end.split(":")[0].toInt()
        return endHour - startHour
    }

    private fun showEventsForDate(date: Date) {
        val eventsForDate = events.filter { it.date.isSameDay(date) }
            .sortedBy { it.attraction.startTimeInt }
        eventAdapter.updateEvents(eventsForDate)
    }

    fun addEvent(attraction: SelectedAttraction, date: Date, durationHours: Int) {
        // Ensure that travel dates are set
        if (takeOffDate == null || landingDate == null) {
            Toast.makeText(requireContext(), "Travel dates are not set", Toast.LENGTH_SHORT).show()
            return
        }

        // Debug logs
        Log.d("CalendarFragment", "Take-off date: ${takeOffDate!!.time}")
        Log.d("CalendarFragment", "Landing date: ${landingDate!!.time}")
        Log.d("CalendarFragment", "Event date: $date")

        // Check if the date is within the allowed range
        val takeOffTime = takeOffDate!!.time
        val landingTime = landingDate!!.time
        if (date.before(takeOffTime) || date.after(landingTime)) {
            Toast.makeText(requireContext(), "Date is outside of your travel period", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the event already exists
        if (events.none { it.attraction.title == attraction.title && it.date.isSameDay(date) }) {
            val newEvent = Event(attraction, date, durationHours)
            if (currentUserEmail != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    userDao.addSelectedAttraction(currentUserEmail, attraction)
                    withContext(Dispatchers.Main) {
                        events.add(newEvent)
                        showEventsForDate(date)
                        val timeRange = calculateTimeRange(date, durationHours)
                        Toast.makeText(requireContext(), "Event Added: ${attraction.title} on ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)} $timeRange", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Event already exists on this date", Toast.LENGTH_SHORT).show()
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

    private fun Date.isSameDay(other: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = this@isSameDay }
        val cal2 = Calendar.getInstance().apply { time = other }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
