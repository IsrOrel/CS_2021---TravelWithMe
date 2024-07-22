package com.example.travelwithme

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
import java.text.SimpleDateFormat
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
        if (currentUserEmail != null) {
            fetchTravelDates()
            setupRecyclerView()
            fetchEvents()
            binding.goBackButton.setOnClickListener {
                findNavController().navigateUp()
            }
        } else {
            Log.e("CalendarFragment", "Current user email is null")
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchTravelDates() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val userData = userDao.getUserByEmail(currentUserEmail!!)
                withContext(Dispatchers.Main) {
                    takeOffDate = Calendar.getInstance().apply { time = userData?.takeOffDate ?: Date() }
                    landingDate = Calendar.getInstance().apply { time = userData?.landingDate ?: Date() }
                    setupCalendarView()
                }
            } catch (e: Exception) {
                Log.e("CalendarFragment", "Error fetching travel dates: ${e.message}", e)
                Toast.makeText(requireContext(), "Error fetching travel dates", Toast.LENGTH_LONG).show()
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
                            set(year, month, dayOfMonth, 0, 0, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.time
                        showEventsForDate(selectedDate)
                    }
                } ?: run {
                    Toast.makeText(requireContext(), "Departure date not set", Toast.LENGTH_LONG).show()
                }
            } ?: run {
                Toast.makeText(requireContext(), "Landing date not set", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("CalendarFragment", "Error setting up CalendarView: ${e.message}", e)
            Toast.makeText(requireContext(), "Error setting up CalendarView", Toast.LENGTH_LONG).show()
        }
    }


    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(events)
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.eventsRecyclerView.adapter = eventAdapter
        Log.d("CalendarFragment", "RecyclerView setup with adapter: $eventAdapter")
    }

    private fun fetchEvents() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val selectedAttractions = userDao.getSelectedAttractions(currentUserEmail!!)
                Log.d("CalendarFragment", "Selected attractions: $selectedAttractions")
                withContext(Dispatchers.Main) {
                    updateEventList(selectedAttractions)
                }
            } catch (e: Exception) {
                Log.e("CalendarFragment", "Error fetching events: ${e.message}", e)
                Toast.makeText(requireContext(), "Error fetching events", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateEventList(selectedAttractions: List<SelectedAttraction>) {
        Log.d("CalendarFragment", "Updating event list with ${selectedAttractions.size} attractions")
        events.clear()
        events.addAll(selectedAttractions.map { attraction ->
            Event(
                attraction = attraction,
                date = attraction.plannedDate,
                durationHours = calculateDurationHours(attraction.plannedTime)
            )
        })
        Log.d("CalendarFragment", "Updated events list: $events")

        events.sortWith(compareBy<Event> { it.date }.thenBy { it.date })
        eventAdapter.notifyDataSetChanged()
        Log.d("CalendarFragment", "Adapter notified of data changes")
    }


    private fun calculateDurationHours(plannedTime: String): Int {
        val (start, end) = plannedTime.split("-")
        val (startHour, startMinute) = start.split(":").map { it.toInt() }
        val (endHour, endMinute) = end.split(":").map { it.toInt() }

        val startInMinutes = startHour * 60 + startMinute
        val endInMinutes = endHour * 60 + endMinute

        return (endInMinutes - startInMinutes) / 60 // Duration in hours
    }


    private fun showEventsForDate(date: Date) {
        val calDate = Calendar.getInstance().apply { time = date }
        val eventsForDate = events.filter {
            val eventCalDate = Calendar.getInstance().apply { time = it.date }
            eventCalDate.get(Calendar.YEAR) == calDate.get(Calendar.YEAR) &&
                    eventCalDate.get(Calendar.MONTH) == calDate.get(Calendar.MONTH) &&
                    eventCalDate.get(Calendar.DAY_OF_MONTH) == calDate.get(Calendar.DAY_OF_MONTH)
        }

        Log.d("CalendarFragment", "Events for date $date: $eventsForDate")
        eventAdapter.updateEvents(eventsForDate)
    }


    fun addEvent(attraction: SelectedAttraction, date: Date, plannedTime: String, durationHours: Int) {
        Log.d("CalendarFragment", "Event date: $date")
        Log.d("CalendarFragment", "Duration Hours: $durationHours")
        Log.d("CalendarFragment", "Planned Time: $plannedTime")

        if (takeOffDate == null || landingDate == null) {
            Toast.makeText(requireContext(), "Travel dates not set", Toast.LENGTH_SHORT).show()
            return
        }

        val timeRange = calculateTimeRange(date, plannedTime, durationHours)
        Log.d("CalendarFragment", "Time Range: $timeRange")

        val takeOffTime = takeOffDate!!.time
        val landingTime = landingDate!!.time
        if (date.before(takeOffTime) || date.after(landingTime)) {
            Toast.makeText(requireContext(), "Date is outside your travel period", Toast.LENGTH_SHORT).show()
            return
        }

        if (events.none { it.attraction.title == attraction.title && it.date.isSameDay(date) }) {
            val newEvent = Event(attraction, date, durationHours)
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    userDao.addSelectedAttraction(currentUserEmail!!, attraction)
                    withContext(Dispatchers.Main) {
                        events.add(newEvent)
                        showEventsForDate(date)
                        Toast.makeText(requireContext(), "Event added: ${attraction.title} on ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)} $timeRange", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("CalendarFragment", "Error adding event: ${e.message}", e)
                    Toast.makeText(requireContext(), "Error adding event", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Event already exists on this date", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateTimeRange(startDate: Date, plannedTime: String, durationHours: Int): String {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val (start, _) = plannedTime.split("-")
        val calendar = Calendar.getInstance().apply { time = startDate }

        // Set start time from plannedTime
        val (startHour, startMinute) = start.split(":").map { it.toInt() }
        calendar.set(Calendar.HOUR_OF_DAY, startHour)
        calendar.set(Calendar.MINUTE, startMinute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // End time
        val endTime = Calendar.getInstance().apply { time = calendar.time }
        endTime.add(Calendar.HOUR_OF_DAY, durationHours)

        return "${formatter.format(calendar.time)}-${formatter.format(endTime.time)}"
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
