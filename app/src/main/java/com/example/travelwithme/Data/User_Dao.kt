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

    // Update hotels
    @Query("UPDATE user_data SET Hotels = :hotels WHERE email = :email")
    fun updateHotels(email: String, hotels: List<Hotels>)

    // Get selected attractions (raw)
    @Query("SELECT selected_attractions FROM user_data WHERE email = :email")
    fun getSelectedAttractionsRaw(email: String): String?

    // Get selected attractions
    fun getSelectedAttractions(email: String): List<SelectedAttraction> {
        val rawData = getSelectedAttractionsRaw(email)
        return if (rawData != null) {
            User_Data_Convertors().fromSelectedAttractionList(rawData)
        } else {
            emptyList()
        }
    }

    // Update selected attractions (raw)
    @Query("UPDATE user_data SET selected_attractions = :attractions WHERE email = :email")
    fun updateSelectedAttractionsRaw(email: String, attractions: String)

    // Update selected attractions
    fun updateSelectedAttractions(email: String, attractions: List<SelectedAttraction>) {
        val rawData = User_Data_Convertors().toSelectedAttractionList(attractions)
        updateSelectedAttractionsRaw(email, rawData)
    }

    // Add a selected attraction
    fun addSelectedAttraction(email: String, attraction: SelectedAttraction) {
        val currentAttractions = getSelectedAttractions(email).toMutableList()
        currentAttractions.add(attraction)
        currentAttractions.sortWith(compareBy<SelectedAttraction> { it.plannedDate }.thenBy { it.startTimeInt })
        updateSelectedAttractions(email, currentAttractions)
    }

    // Add a hotel
    @Transaction
    fun addHotel(email: String, newHotel: Hotels) {
        val user = getUserByEmail(email)
        user?.let {
            val updatedHotels = it.hotels + newHotel
            updateHotels(email, updatedHotels)
        }
    }

    // Update a specific hotel in the list
    @Transaction
    fun updateHotel(email: String, updatedHotel: Hotels) {
        val user = getUserByEmail(email)
        user?.let { currentUser ->
            val updatedHotels = currentUser.hotels.map { hotel ->
                if (hotel.name == updatedHotel.name && hotel.address == updatedHotel.address) {
                    updatedHotel
                } else {
                    hotel
                }
            }
            updateHotels(email, updatedHotels)
        }
    }

    // Remove a hotel from the list
    @Transaction
    fun removeHotel(email: String, hotelToRemove: Hotels) {
        val user = getUserByEmail(email)
        user?.let { currentUser ->
            val updatedHotels = currentUser.hotels.filter { hotel ->
                hotel.name != hotelToRemove.name || hotel.address != hotelToRemove.address
            }
            updateHotels(email, updatedHotels)
        }
    }

    // Get take-off date
    @Query("SELECT take_off_date FROM user_data WHERE email = :email")
    fun getTakeOffDate(email: String): Long?

    // Get landing date
    @Query("SELECT landing_date FROM user_data WHERE email = :email")
    fun getLandingDate(email: String): Long?

    // Get destination
    @Query("SELECT destination FROM user_data WHERE email = :email")
    fun getDestination(email: String): String?

    // Update checklist
    @Query("UPDATE user_data SET checklist = :updatedChecklist WHERE email = :email")
    fun updateChecklist(email: String, updatedChecklist: List<ChecklistItem>)

    // Update a specific checklist item
    @Transaction
    fun updateChecklistItem(email: String, updatedItem: ChecklistItem) {
        val user = getUserByEmail(email)
        user?.let {
            val updatedChecklist = it.checklist.map { item ->
                if (item.id == updatedItem.id) updatedItem else item
            }
            updateChecklist(email, updatedChecklist)
        }
    }

    // Add a checklist item
    @Transaction
    fun addChecklistItem(email: String, newItem: ChecklistItem) {
        val user = getUserByEmail(email)
        user?.let {
            val updatedChecklist = it.checklist + newItem
            updateChecklist(email, updatedChecklist)
        }
    }

    // Delete a checklist item
    @Transaction
    fun deleteChecklistItem(email: String, itemToDelete: ChecklistItem) {
        val user = getUserByEmail(email)
        user?.let {
            val updatedChecklist = it.checklist.filter { item -> item.id != itemToDelete.id }
            updateChecklist(email, updatedChecklist)
        }
    }
}