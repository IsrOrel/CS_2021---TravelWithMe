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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CalendarBinding.inflate(inflater, container, false)
        userDao = TravelDatabase.getInstance(requireContext()).userDao()
        return binding.root
    }

    // Fetch current user's email
    private val currentUserEmail = UserSession.getCurrentUserEmail()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup go back button
        binding.goBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Setup calendar view date change listener
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            showEventsForDate(selectedDate)
        }

        // Setup RecyclerView
        eventAdapter = EventAdapter(events)
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.eventsRecyclerView.adapter = eventAdapter
        fetchSelectedAttractions()

    }

    private fun fetchSelectedAttractions() {
        val currentUserEmail = UserSession.getCurrentUserEmail()
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
                durationHours = 1 // You might want to store and use the actual duration
            )
        })
        showEventsForDate(Calendar.getInstance().time)
    }

    // Method to add event
    fun addEvent(attraction: SelectedAttraction, date: Date, durationHours: Int) {
        if (events.none { it.attraction.title == attraction.title && it.date.isSameDay(date) }) {
            val newEvent = Event(attraction, date, durationHours)
            if (currentUserEmail != null) {
                // Add attraction to database
                userDao.addAttraction(currentUserEmail, attraction)
            }
            events.add(newEvent)
            showEventsForDate(date)
            val timeRange = calculateTimeRange(date, durationHours)
            Toast.makeText(requireContext(), "Event Added: ${attraction.title} on ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)} $timeRange", Toast.LENGTH_SHORT).show()
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

    private fun showEventsForDate(date: Date) {
        val eventsForDate = events.filter { it.date.isSameDay(date) }
        eventAdapter.updateEvents(eventsForDate)
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
