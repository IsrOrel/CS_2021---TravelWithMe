package com.example.travelwithme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.travelwithme.databinding.HomeScreenBinding

class Home_screen : Fragment() {

    private var _binding: HomeScreenBinding? = null
    private val binding get() = _binding!!

    private var isExpanded = false
    private var isFabMenuOpen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFabMenu()

        binding.AttractionBtn.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_attractions)
        }

        binding.CarRent.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_home_screen)
        }

        binding.Calendar.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_calendar)
        }

        binding.facts.setOnClickListener {
            if (isExpanded) {
                binding.textViewContainer.visibility = View.GONE
                binding.facts.text = "Fun facts about the city:"
            } else {
                binding.textViewContainer.visibility = View.VISIBLE
                binding.facts.text = "Fun facts about the city:"
            }
            isExpanded = !isExpanded
        }

        binding.fabMenuOverlay.setOnClickListener { toggleFabMenu() }
    }

    private fun setupFabMenu() {
        binding.floatingActionButton.setOnClickListener {
            toggleFabMenu()
        }
        // Set initial translation for fab menu (off-screen upwards)
        binding.fabMenu.translationY = -resources.displayMetrics.heightPixels.toFloat()

        // Set click listeners for each sub FAB
        binding.fabFlight.setOnClickListener { /* Handle flight click */ }
        binding.fabHotel.setOnClickListener {
            findNavController().navigate(R.id.action_home_screen_to_add_Hotel) }
        binding.fabAttractions.setOnClickListener { /* Handle attractions click */ }
        binding.fabCarRental.setOnClickListener { /* Handle car rental click */ }
        binding.fabCalendar.setOnClickListener { /* Handle calendar click */ }
        binding.fabNotes.setOnClickListener { /* Handle notes click */ }
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

        // Bring the FAB menu to the front
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}