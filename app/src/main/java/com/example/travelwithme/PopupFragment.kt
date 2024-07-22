package com.example.travelwithme

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class PopupFragment : DialogFragment() {
    private var _binding: FragmentPopupBinding? = null
    private val binding get() = _binding!!
    private lateinit var userDao: User_Dao
    private val currentUserEmail = UserSession.getCurrentUserEmail()

    interface OnDateTimeSelectedListener {
        fun onDateTimeSelected(date: Date, durationHours: Int)
    }

    var listener: OnDateTimeSelectedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize userDao here
        userDao = TravelDatabase.getInstance(requireContext()).userDao()
    }

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

        // Fetch takeoff and landing dates and set them to the DatePicker
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val takeOffDateTimestamp = currentUserEmail?.let { userDao.getTakeOffDate(it) }
                val landingDateTimestamp = currentUserEmail?.let { userDao.getLandingDate(it) }
                withContext(Dispatchers.Main) {
                    if (takeOffDateTimestamp != null && landingDateTimestamp != null) {
                        val takeOffDate = Date(takeOffDateTimestamp)
                        val landingDate = Date(landingDateTimestamp)
                        binding.datePicker.minDate = takeOffDate.time
                        binding.datePicker.maxDate = landingDate.time
                    }
                }
            } catch (e: Exception) {
                Log.e("PopupFragment", "Error fetching travel dates: ${e.message}", e)
            }
        }

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
                        binding.datePicker.dayOfMonth
                    )
                }

                val selectedDuration = binding.durationSpinner.selectedItem.toString()
                val durationHours = selectedDuration.split(" ")[0].toInt()

                // Pass the selected date and duration to the listener
                listener?.onDateTimeSelected(calendar.time, durationHours)
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

