package com.example.travelwithme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.travelwithme.databinding.SignInBinding

class Sign_In : Fragment() {

    private var _binding: SignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            // TODO: Implement sign in logic
            Toast.makeText(context, "Sign In Clicked: $email", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_sign_In_to_my_Trips)
        }

        binding.signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_sign_In_to_sign_Up)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}