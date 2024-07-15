package com.example.travelwithme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.example.travelwithme.databinding.CalanderBinding


class Calander : Fragment() {
    private var __binding : CalanderBinding? = null
    private val binding get() = __binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        __binding=CalanderBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        __binding = null
    }
}