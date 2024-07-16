package com.example.travelwithme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.travelwithme.databinding.AddFlightBinding

class Add_flight : Fragment() {
    private var __binding : AddFlightBinding? = null
    private val binding get() = __binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        __binding=AddFlightBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        __binding = null
    }
}