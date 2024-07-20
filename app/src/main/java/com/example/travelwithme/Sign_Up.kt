package com.example.travelwithme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.travelwithme.Data.TravelDatabase
import com.example.travelwithme.Data.UserSession
import com.example.travelwithme.Data.User_Dao
import com.example.travelwithme.Data.User_Data
import com.example.travelwithme.databinding.SignUpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date


class Sign_Up : Fragment() {

    private lateinit var binding: SignUpBinding
    private lateinit var userDao: User_Dao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDao = TravelDatabase.getInstance(requireContext()).userDao()

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInputs(email, name, password)) {
                signUpUser(email, name, password)
                UserSession.setCurrentUser(email)
                findNavController().navigate(R.id.action_sign_Up_to_my_Trips)
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun signUpUser(email: String, name: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())
            val user = User_Data(
                email = email,
                name = name,
                password = hashedPassword,
                takeOffDate = Date(),
                landingDate = Date(),
                destination = "",
                selectedAttractions = emptyList()
            )

            try {
                userDao.insertOrUpdateUser(user)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT).show()
                    // Navigate to next screen if needed
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error registering user: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun validateInputs(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            showError("Name cannot be empty")
            return false
        }

        if (!isValidEmail(email)) {
            showError("Please enter a valid email address")
            return false
        }

        if (!isValidPassword(password)) {
            showError("Password must be at least 6 characters long and contain at least one number")
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        // Password must be at least 6 characters long and contain at least one number
        val passwordRegex = "^(?=.*[0-9]).{6,}$".toRegex()
        return passwordRegex.matches(password)
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}