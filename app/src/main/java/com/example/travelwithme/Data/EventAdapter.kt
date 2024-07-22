package com.example.travelwithme

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwithme.Data.Event
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EventAdapter(private var events: List<Event>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    fun updateEvents(newEvents: List<Event>) {
        this.events = newEvents
        Log.d("EventAdapter", "Events updated: $newEvents")
        notifyDataSetChanged() // This ensures that the RecyclerView is updated
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = events.size

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.eventImage)
        private val eventImage: ImageView = itemView.findViewById(R.id.eventImage)
        val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
        val eventDescription: TextView = itemView.findViewById(R.id.eventDescription)
        val eventTime: TextView = itemView.findViewById(R.id.eventTime)

        fun bind(event: Event) {
            eventTitle.text = event.attraction.title ?: "No Title"
            eventTime.text = calculateTimeRange(event.date, event.durationHours)

            val categoryIconResId = CategoryIcons.getIconForCategory(event.attraction.category ?: "Unknown")
            Log.d("EventAdapter", "Category: ${event.attraction.category}, Icon Res ID: $categoryIconResId")
            if (categoryIconResId != 0) {
                eventImage.setImageResource(categoryIconResId)
            } else {
                eventImage.setImageResource(R.drawable.icon_all)
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
    }
}

