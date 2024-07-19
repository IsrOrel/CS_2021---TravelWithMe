package com.example.travelwithme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.travelwithme.databinding.HomeScreenBinding

class Home_screen : Fragment() {
    private var __binding : HomeScreenBinding? = null
    private val binding get() = __binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        __binding = HomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.AttractionBtn.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_attractions)
        }

        binding.CarRent.setOnClickListener {
            findNavController().navigate(R.id.action_calendar_to_home_screen)
        }

        binding.Calendar.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_calendar)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        __binding = null
    }
}
