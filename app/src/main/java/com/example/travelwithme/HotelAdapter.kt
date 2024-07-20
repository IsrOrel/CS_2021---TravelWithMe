package com.example.travelwithme

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwithme.Data.Hotels
import com.example.travelwithme.databinding.ItemHotelBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HotelAdapter(private val onItemClick: (Hotels) -> Unit) :
    ListAdapter<Hotels, HotelAdapter.HotelViewHolder>(HotelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val binding = ItemHotelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HotelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HotelViewHolder(private val binding: ItemHotelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick(getItem(adapterPosition))
            }
        }

        fun bind(hotel: Hotels) {
            binding.hotelNameTextView.text = hotel.name
            binding.hotelAddressTextView.text = hotel.address
            binding.checkInDateTextView.text = "Check-in: ${formatDate(hotel.CheckinDate)}"
            binding.checkOutDateTextView.text = "Check-out: ${formatDate(hotel.CheckoutDate)}"
        }

        private fun formatDate(date: java.util.Date): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return sdf.format(date)
        }
    }

    class HotelDiffCallback : DiffUtil.ItemCallback<Hotels>() {
        override fun areItemsTheSame(oldItem: Hotels, newItem: Hotels): Boolean {
            return oldItem.name == newItem.name && oldItem.address == newItem.address
        }

        override fun areContentsTheSame(oldItem: Hotels, newItem: Hotels): Boolean {
            return oldItem == newItem
        }
    }
}