package com.example.travelwithme.Data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface User_Dao {
    // Insert or update a full User_Data object
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateUser(userData: User_Data)

    // Get user by email
    @Query("SELECT * FROM User_Data WHERE email = :email")
    fun getUserByEmail(email: String): User_Data?

    // Get all users
    @Query("SELECT * FROM user_data")
    fun getAllUsers(): List<User_Data>

    // Update destination
    @Query("UPDATE user_data SET destination = :newDestination WHERE email = :email")
    fun updateDestination(email: String, newDestination: String)

    // Update trip dates
    @Query("UPDATE user_data SET take_off_date = :startDate, landing_date = :endDate WHERE email = :email")
    fun updateTripDates(email: String, startDate: Long, endDate: Long)

    // Add a new attraction to the list
    @Transaction
    fun addAttraction(email: String, newAttraction: SelectedAttraction) {
        val user = getUserByEmail(email)
        user?.let {
            val updatedAttractions = it.selectedAttractions + newAttraction
            updateAttractions(email, updatedAttractions)
        }
    }

    // Remove an attraction from the list
    @Transaction
    fun removeAttraction(email: String, attractionToRemove: SelectedAttraction) {
        val user = getUserByEmail(email)
        user?.let {
            val updatedAttractions = it.selectedAttractions.filter { attraction ->
                attraction.title != attractionToRemove.title ||
                        attraction.plannedDate != attractionToRemove.plannedDate ||
                        attraction.plannedTime != attractionToRemove.plannedTime
            }
            updateAttractions(email, updatedAttractions)
        }
    }

    // Update the entire attractions list
    @Query("UPDATE user_data SET selected_attractions = :attractions WHERE email = :email")
    fun updateAttractions(email: String, attractions: List<SelectedAttraction>)


    // Delete a user
    @Query("DELETE FROM user_data WHERE email = :email")
    fun deleteUser(email: String)

    // Count total users
    @Query("SELECT COUNT(*) FROM user_data")
    fun getUserCount(): Int
}

