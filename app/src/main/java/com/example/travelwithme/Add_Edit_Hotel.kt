package com.example.travelwithme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.travelwithme.Data.Hotels
import com.example.travelwithme.Data.TravelDatabase
import com.example.travelwithme.Data.UserSession
import com.example.travelwithme.Data.User_Dao
import com.example.travelwithme.databinding.FragmentAddEditHotelBinding
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Add_Edit_Hotel : Fragment() {

    private var _binding: FragmentAddEditHotelBinding? = null
    private val binding get() = _binding!!
    private lateinit var userDao: User_Dao

    private var checkInDate: Date? = null
    private var checkOutDate: Date? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditHotelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDao = TravelDatabase.getInstance(requireContext()).userDao()

        binding.checkInDateTextView.setOnClickListener { showDatePicker(true) }
        binding.checkOutDateTextView.setOnClickListener { showDatePicker(false) }
        binding.saveHotelButton.setOnClickListener { saveHotel() }
    }

    private fun showDatePicker(isCheckIn: Boolean) {
        val titleText = if (isCheckIn) "Select Check-in Date" else "Select Check-out Date"
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(titleText)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Date(selection)
            if (isCheckIn) {
                checkInDate = selectedDate
                binding.checkInDateTextView.text = "Check-in Date: ${formatDate(selectedDate)}"
            } else {
                if (checkInDate != null && selectedDate > checkInDate) {
                    checkOutDate = selectedDate
                    binding.checkOutDateTextView.text = "Check-out Date: ${formatDate(selectedDate)}"
                } else {
                    Toast.makeText(context, "Check-out date must be after check-in date", Toast.LENGTH_LONG).show()
                }
            }
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    private fun saveHotel() {
        val name = binding.hotelNameEditText.text.toString()
        val address = binding.hotelAddressEditText.text.toString()

        if (name.isBlank() || address.isBlank() || checkInDate == null || checkOutDate == null) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_LONG).show()
            return
        }

        val hotel = Hotels(name, address, checkInDate!!, checkOutDate!!)
        val userEmail = UserSession.getCurrentUserEmail()

        if (userEmail != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    userDao.addHotel(userEmail, hotel)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Hotel saved successfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error saving hotel: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Toast.makeText(context, "Error: User not logged in", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}