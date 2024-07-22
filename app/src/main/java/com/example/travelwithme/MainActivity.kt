package com.example.travelwithme

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.travelwithme.Data.DataSeeder
import com.example.travelwithme.Data.UserSession
import androidx.activity.addCallback
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Seed data when the app starts
        DataSeeder(this).seedData()

        // Set up the NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Handle back press for the entire app
        onBackPressedDispatcher.addCallback(this) {
            when (navController.currentDestination?.id) {
                R.id.my_Trips -> finish()
                R.id.home_screen -> {
                    // Navigate back to My_Trips from home screen
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.home_screen, true)
                        .build()
                    navController.navigate(R.id.action_home_screen_to_my_Trips, null, navOptions)
                }
                else -> navController.navigateUp()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        UserSession.logout()
    }
}