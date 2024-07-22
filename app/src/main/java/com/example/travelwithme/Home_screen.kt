package com.example.travelwithme

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.travelwithme.Data.TravelDatabase
import com.example.travelwithme.Data.UserSession
import com.example.travelwithme.Data.User_Dao
import com.example.travelwithme.databinding.HomeScreenBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import android.content.Intent
import android.net.Uri
import android.app.AlertDialog
import android.util.Log
import android.widget.Button
import android.widget.TextView
import java.time.format.DateTimeFormatter

class Home_screen : Fragment() {

    private var _binding: HomeScreenBinding? = null
    private val binding get() = _binding!!

    private var isExpanded = false
    private var isFabMenuOpen = false
    private lateinit var userDao: User_Dao
    private lateinit var usersession: UserSession

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = TravelDatabase.getInstance(requireContext())
        userDao = db.userDao()



        val currentUserEmail = UserSession.getCurrentUserEmail()
        if(currentUserEmail != null) {
            lifecycleScope.launch {
                val destination = withContext(Dispatchers.IO) {
                    userDao.getDestination(currentUserEmail)
                }
                Log.d("Home_screen", "Fetched destination: $destination")
                binding.TripCity.text = destination
                binding.flightnext.text = destination

                // Set facts for the destination
                if (destination != null) {
                    setFactsForDestination(destination)
                }

                val countApp = withContext(Dispatchers.IO) {
                    userDao.getTakeOffDate(currentUserEmail)
                }
                if (countApp != null) {
                    val timestamp = countApp.toLong()
                    val appDate = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()

                    // Get the current date
                    val currentDate = LocalDate.now()

                    // Calculate the difference in days
                    val daysBetween = ChronoUnit.DAYS.between(currentDate, appDate)
                    binding.countdown.text = "$daysBetween days"
                }

                val Uname = withContext(Dispatchers.IO) {
                    userDao.getUserByEmail(currentUserEmail)
                }
                if (Uname != null) {
                    binding.userName.text = Uname.name
                }
            }
        }

        setupFabMenu()

        binding.AttractionBtn.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_attractions)
        }

        binding.CarRent.setOnClickListener {
            val url = "https://www.carrentals.com/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        binding.Calendar.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_calendarFragment)
        }

        binding.checkListBtn.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_checkList)
        }

        binding.facts.setOnClickListener {
            if (isExpanded) {
                binding.textViewContainer.visibility = View.GONE
                binding.facts.text = "Fun facts about the city:"
            } else {
                binding.textViewContainer.visibility = View.VISIBLE
                binding.facts.text = "Hide fun facts"
            }
            isExpanded = !isExpanded
        }

        binding.fabMenuOverlay.setOnClickListener { toggleFabMenu() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupFabMenu() {
        binding.floatingActionButton.setOnClickListener {
            toggleFabMenu()
        }
        binding.fabMenu.translationY = -resources.displayMetrics.heightPixels.toFloat()

        binding.fabFlight.setOnClickListener { showFlightInfoDialog() }
        binding.fabHotel.setOnClickListener {
            val action = Home_screenDirections.actionHomeScreenToAddHotel(fromHomeScreen = true)
            findNavController().navigate(action)
        }
        binding.fabAttractions.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_attractions)
        }
        binding.fabCarRental.setOnClickListener {
            val url = "https://www.carrentals.com/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        binding.fabCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_calendarFragment)
        }
        binding.fabNotes.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_checkList)
        }
    }

    private fun toggleFabMenu() {
        if (isFabMenuOpen) {
            hideFabMenu()
        } else {
            showFabMenu()
        }
        isFabMenuOpen = !isFabMenuOpen
    }

    private fun showFabMenu() {
        binding.fabMenu.visibility = View.VISIBLE
        binding.fabMenuOverlay.visibility = View.VISIBLE
        binding.floatingActionButton.animate().rotation(135f)
        binding.fabMenu.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(300)
            .start()
        binding.fabMenuOverlay.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
        binding.fabMenu.bringToFront()
    }

    private fun hideFabMenu() {
        binding.floatingActionButton.animate().rotation(0f)
        binding.fabMenu.animate()
            .translationY(-resources.displayMetrics.heightPixels.toFloat())
            .setDuration(300)
            .withEndAction {
                binding.fabMenu.visibility = View.GONE
            }
            .start()
        binding.fabMenuOverlay.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                binding.fabMenuOverlay.visibility = View.GONE
            }
            .start()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showFlightInfoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout_flight_info, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.closeButton).setOnClickListener {
            dialog.dismiss()
        }

        val currentUserEmail = UserSession.getCurrentUserEmail()
        if (currentUserEmail != null) {
            lifecycleScope.launch {
                val destination = withContext(Dispatchers.IO) {
                    userDao.getDestination(currentUserEmail)
                }
                val takeOffDate = withContext(Dispatchers.IO) {
                    userDao.getTakeOffDate(currentUserEmail)
                }
                val landingDate = withContext(Dispatchers.IO) {
                    userDao.getLandingDate(currentUserEmail)
                }

                dialogView.findViewById<TextView>(R.id.flightToAnswer).text = destination ?: "Not set"

                if (takeOffDate != null) {
                    val departureDate = Instant.ofEpochMilli(takeOffDate.toLong())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    dialogView.findViewById<TextView>(R.id.departureDateAnswer).text =
                        departureDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } else {
                    dialogView.findViewById<TextView>(R.id.departureDateAnswer).text = "Not set"
                }

                if (landingDate != null) {
                    val returnLocalDate = Instant.ofEpochMilli(landingDate.toLong())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    dialogView.findViewById<TextView>(R.id.returnDateAnswer).text =
                        returnLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } else {
                    dialogView.findViewById<TextView>(R.id.returnDateAnswer).text = "Not set"
                }
            }
        }

        dialog.show()
    }

    private fun getFactsForCity(city: String): Array<String> {
        return when {
            city.contains("London", ignoreCase = true) -> resources.getStringArray(R.array.facts_london)
            city.contains("Amsterdam", ignoreCase = true) -> resources.getStringArray(R.array.facts_amsterdam)
            city.contains("Rome", ignoreCase = true) -> resources.getStringArray(R.array.facts_rome)
            else -> arrayOf("No facts available for this city")
        }
    }

    private fun setFactsForDestination(destination: String) {
        val facts = getFactsForCity(destination)
        binding.fact1.text = facts.getOrNull(0) ?: "No fact available"
        binding.fact2.text = facts.getOrNull(1) ?: "No fact available"
        binding.fact3.text = facts.getOrNull(2) ?: "No fact available"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}