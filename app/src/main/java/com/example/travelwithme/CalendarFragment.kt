package com.example.travelwithme

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.travelwithme.Data.Attraction_Data
import com.example.travelwithme.databinding.CalendarBinding
import com.example.travelwithme.CalendarFragmentArgs
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

        // Get arguments and add event
        val args: CalendarFragmentArgs by navArgs()
        val attractionTitle = args.attractionTitle
        val eventDate = Date(args.eventDate)

        val attraction = Attraction_Data(
            id = 0, // or appropriate id
            image = R.drawable.icon_beach, // replace with appropriate image resource
            title = attractionTitle,
            description = "Description", // replace with actual description if available
            place = "Place" // replace with actual place if available
        )

        addEvent(attraction, eventDate)
    }

    fun addEvent(attraction: Attraction_Data, date: Date) {
        events.add(Event(attraction, date))
        showEventsForDate(date)
        Toast.makeText(requireContext(), "Event Added: ${attraction.title} on ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)}", Toast.LENGTH_SHORT).show()
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
            eventView.findViewById<TextView>(R.id.eventTime).text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(event.date)

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

    data class Event(val attraction: Attraction_Data, val date: Date)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
