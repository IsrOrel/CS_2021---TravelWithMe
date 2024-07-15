package com.example.travelwithme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.travelwithme.databinding.MyTripsBinding


class My_Trips : Fragment() {
    private var __binding : MyTripsBinding? = null
    private val binding get() = __binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        __binding=MyTripsBinding.inflate(inflater,container,false)
        return binding.root

    }
    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ThisTrip.setOnClickListener {
            findNavController().navigate(R.id.action_my_Trips_to_home_screen)
        }
        binding.AddTrip.setOnClickListener {
            findNavController().navigate(R.id.action_my_Trips_to_add_flight)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        __binding = null
    }
}