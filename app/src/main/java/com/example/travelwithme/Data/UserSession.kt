package com.example.travelwithme.Data

object UserSession {
    private var currentUserEmail: String? = null

    fun setCurrentUser(email: String) {
        currentUserEmail = email
    }

    fun getCurrentUserEmail(): String? {
        return currentUserEmail
    }


    fun logout() {
        currentUserEmail = null
    }
}