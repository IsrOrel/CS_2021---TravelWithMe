package com.example.travelwithme

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.travelwithme.Data.Attraction_Data
import com.example.travelwithme.Data.Event
import com.example.travelwithme.databinding.CalendarBinding
import java.util.*

class CalendarFragment : Fragment() {
    private var _binding: CalendarBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding should not be null")

    private val events = mutableListOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

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

        // Get arguments
        val args: CalendarFragmentArgs by navArgs()

        val attractionTitle = args.attractionTitle
        val eventDate = args.eventDate
        val durationHours = args.durationHours

        if (attractionTitle != null && eventDate != null) {
            val date = Date(eventDate)

            // Create an Attraction_Data object
            val attraction = Attraction_Data(
                id = 0,
                title = attractionTitle,
                description = "Description", // replace with actual description if available
                city = "City",
                address = "Address",
                category = "Category", // Set to a default or fetched value
                image = R.drawable.icon_all // Set a default image or handle as needed
            )

            // Add event if it doesn't already exist
            if (events.none { it.attraction.title == attraction.title && it.date.isSameDay(date) }) {
                addEvent(attraction, date, durationHours)
            }
        } else {
            Toast.makeText(requireContext(), "Event data is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addEvent(attraction: Attraction_Data, date: Date, durationHours: Int) {
        events.add(Event(attraction, date, durationHours))
        showEventsForDate(date)
        val timeRange = calculateTimeRange(date, durationHours)
        Toast.makeText(requireContext(), "Event Added: ${attraction.title} on ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)} $timeRange", Toast.LENGTH_SHORT).show()
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

        // Clear previous events
        binding.eventsContainer.removeAllViews()

        // Add events to the UI
        for (event in eventsForDate) {
            val eventView = layoutInflater.inflate(R.layout.item_event, binding.eventsContainer, false)
            eventView.findViewById<TextView>(R.id.eventTitle).text = event.attraction.title
            eventView.findViewById<TextView>(R.id.eventDescription).text = event.attraction.description
            val timeRange = calculateTimeRange(event.date, event.durationHours)
            eventView.findViewById<TextView>(R.id.eventTime).text = timeRange

            // Set the image resource based on the category
            val eventImageView = eventView.findViewById<ImageView>(R.id.eventImage)
            val categoryIconResId = CategoryIcons.getIconForCategory(event.attraction.category)
            eventImageView.setImageResource(categoryIconResId)

            binding.eventsContainer.addView(eventView)
        }
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





