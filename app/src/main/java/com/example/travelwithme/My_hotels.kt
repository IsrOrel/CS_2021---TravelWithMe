package com.example.travelwithme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.travelwithme.databinding.MyHotelsBinding

class My_hotels : Fragment() {
    private var __binding : MyHotelsBinding? = null
    private val binding get() = __binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        __binding=MyHotelsBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.Done.setOnClickListener {
            findNavController().navigate(R.id.action_add_Hotel_to_home_screen)
        }
        binding.Skip.setOnClickListener {
            findNavController().navigate(R.id.action_add_Hotel_to_home_screen)
        }
        binding.Return.setOnClickListener {
            findNavController().navigate(R.id.action_add_Hotel_to_add_flight)
        }
        binding.AddHotel.setOnClickListener {
            findNavController().navigate(R.id.action_add_Hotel_to_add_Edit_Hotel)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        __binding = null
    }
}