package com.example.travelwithme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.travelwithme.databinding.AddFlightBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Add_flight : Fragment() {
    private lateinit var selectedDate: TextView
    private lateinit var datePicker: Button
    private var _binding: AddFlightBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddFlightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val citiesSpinner: Spinner = binding.citiesspinner
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.cities,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citiesSpinner.adapter = adapter

        binding.doneBtn.setOnClickListener {
            findNavController().navigate(R.id.action_add_flight_to_add_Hotel)
        }

        binding.return1.setOnClickListener {
            findNavController().navigate(R.id.action_add_flight_to_my_Trips)
        }

        selectedDate = binding.selectedDate
        datePicker = binding.datePicker
        datePicker.setOnClickListener { datePickerDialog() }
    }

    private fun datePickerDialog() {
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("Select a date")

        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            // Formatting the selected date as a string
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDateString = sdf.format(Date(selection))

            // Displaying the selected date in the TextView
            selectedDate.text = selectedDateString
        }

        datePicker.show(parentFragmentManager, "datePicker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
