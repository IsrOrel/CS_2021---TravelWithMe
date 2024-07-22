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

            lifecycleScope.launch {
                if (validateInputs(email, name, password)) {
                    signUpUser(email, name, password)
                }
            }
        }

        binding.signInButton.setOnClickListener {
            findNavController().navigate(R.id.action_sign_Up_to_sign_In)
        }
    }

    private suspend fun signUpUser(email: String, name: String, password: String) {
        withContext(Dispatchers.IO) {
            val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())
            val user = User_Data(
                email = email,
                name = name,
                password = hashedPassword,
                takeOffDate = Date(),
                landingDate = Date(),
                destination = "",
                selectedAttractions = emptyList(),
                hotels = emptyList(),
                checklist = emptyList()
            )

            try {
                userDao.insertOrUpdateUser(user)
                withContext(Dispatchers.Main) {
                    UserSession.setCurrentUser(email)
                    showToast("User registered successfully")
                    findNavController().navigate(R.id.action_sign_Up_to_my_Trips)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error registering user: ${e.message}")
                }
            }
        }
    }

    private suspend fun validateInputs(email: String, name: String, password: String): Boolean {
        if (name.isEmpty()) {
            showToast("Name cannot be empty")
            return false
        }

        if (!isValidEmailFormat(email)) {
            showToast("Please enter a valid email address")
            return false
        }

        if (!isEmailAvailable(email)) {
            showToast("This email is already registered")
            return false
        }

        if (!isValidPassword(password)) {
            showToast("Password must be at least 6 characters long and contain at least one number")
            return false
        }

        return true
    }

    private fun isValidEmailFormat(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))
    }

    private suspend fun isEmailAvailable(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            val existingUser = userDao.getUserByEmail(email)
            existingUser == null
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[0-9]).{6,}$".toRegex()
        return passwordRegex.matches(password)
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}