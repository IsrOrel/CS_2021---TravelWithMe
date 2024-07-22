package com.example.travelwithme

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.travelwithme.Data.TravelDatabase
import com.example.travelwithme.Data.UserSession
import com.example.travelwithme.Data.User_Dao
import com.example.travelwithme.databinding.FragmentPopupBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.Calendar
import java.util.Date

class PopupFragment : DialogFragment() {

    private lateinit var binding: FragmentPopupBinding
    var listener: OnDateTimeSelectedListener? = null

    interface OnDateTimeSelectedListener {
        fun onDateTimeSelected(date: Date, durationHours: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        binding = FragmentPopupBinding.inflate(inflater)
        builder.setView(binding.root)
            .setPositiveButton("Confirm") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel") { _, _ -> dismiss() }
        return builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the DatePicker
        binding.datePicker.init(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
            null
        )

        // Set up the TimePicker
        binding.timePicker.setIs24HourView(true)

        // Set up the Spinner
        val durationOptions = resources.getStringArray(R.array.durations)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, durationOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.durationSpinner.adapter = adapter
    }

    private fun onConfirm() {
        if (listener == null) {
            Log.e("PopupFragment", "No listener attached!")
            dismiss()
            return
        }

        try {
            val calendar = Calendar.getInstance().apply {
                set(
                    binding.datePicker.year,
                    binding.datePicker.month,
                    binding.datePicker.dayOfMonth
                )
            }

            // Set selected time from TimePicker
            calendar.set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            calendar.set(Calendar.MINUTE, binding.timePicker.minute)
            calendar.set(Calendar.SECOND, 0) // Optional: Set seconds to 0 for consistency

            val selectedDuration = binding.durationSpinner.selectedItem.toString()
            val durationHours = selectedDuration.split(" ")[0].toInt()

            // Log current time
            val currentCalendar = Calendar.getInstance()
            Log.d("PopupFragment", "Current Time: ${currentCalendar.time}")

            // Log selected time
            Log.d("PopupFragment", "Selected Time: ${calendar.time}")

            listener?.onDateTimeSelected(calendar.time, durationHours)
        } catch (e: Exception) {
            Log.e("PopupFragment", "Error processing event: ${e.message}", e)
        } finally {
            dismiss()
        }
    }

    fun setOnDateTimeSelectedListener(listener: OnDateTimeSelectedListener) {
        this.listener = listener
    }
}

