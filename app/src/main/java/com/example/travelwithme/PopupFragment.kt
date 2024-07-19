package com.example.travelwithme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.travelwithme.databinding.FragmentPopupBinding
import java.util.*

class PopupFragment : DialogFragment() {
    private var _binding: FragmentPopupBinding? = null
    private val binding get() = _binding!!

    interface OnDateTimeSelectedListener {
        fun onDateTimeSelected(date: Date)
    }

    var listener: OnDateTimeSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup spinner
        val durations = resources.getStringArray(R.array.durations)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, durations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.durationSpinner.adapter = adapter

        binding.confirmButton.setOnClickListener {
            if (listener == null) {
                Log.e("PopupFragment", "No listener attached!")
                dismiss()
                return@setOnClickListener
            }

            try {
                val calendar = Calendar.getInstance().apply {
                    set(
                        binding.datePicker.year,
                        binding.datePicker.month,
                        binding.datePicker.dayOfMonth,
                        binding.timePicker.hour,
                        binding.timePicker.minute
                    )
                }

                val selectedDuration = binding.durationSpinner.selectedItem.toString()
                val durationHours = selectedDuration.split(" ")[0].toInt()

                val endCalendar = calendar.clone() as Calendar
                endCalendar.add(Calendar.HOUR_OF_DAY, durationHours)

                // Pass the selected date to the listener
                listener?.onDateTimeSelected(calendar.time)
            } catch (e: Exception) {
                Log.e("PopupFragment", "Error processing event: ${e.message}", e)
            } finally {
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

