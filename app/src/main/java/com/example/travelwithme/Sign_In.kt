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
import com.example.travelwithme.databinding.SignInBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Sign_In : Fragment() {

    private var _binding: SignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var userDao: User_Dao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = TravelDatabase.getInstance(requireContext())
        userDao = db.userDao()
        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            } else {
                Toast.makeText(context, "Please enter both email and password", Toast.LENGTH_SHORT)
                    .show()
            }
            findNavController().navigate(R.id.action_sign_In_to_my_Trips)
        }

        binding.signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_sign_In_to_sign_Up)
        }
    }
    private fun verifyPassword(enteredPassword: String, storedHash: String): Boolean {
        return BCrypt.verifyer().verify(enteredPassword.toCharArray(), storedHash).verified
    }
    private fun signIn(email: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val user = userDao.getUserByEmail(email)

            if (user != null && verifyPassword(password, user.password)) {
                // Sign in successful
                UserSession.setCurrentUser(email)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Sign in successful", Toast.LENGTH_SHORT).show()
                    view?.post {
                        findNavController().navigate(R.id.action_sign_In_to_my_Trips)
                    }
                }
            } else {
                // Sign in failed
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}