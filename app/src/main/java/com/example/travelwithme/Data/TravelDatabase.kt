// TravelDatabase.kt
package com.example.travelwithme.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User_Data::class, Attraction_Data::class], version = 3, exportSchema = false)
@TypeConverters(User_Data_Convertors::class)
abstract class TravelDatabase : RoomDatabase() {
    abstract fun userDao(): User_Dao
    abstract fun attractionDao(): AttractionDao

    companion object {
        @Volatile
        private var INSTANCE: TravelDatabase? = null

        fun getInstance(context: Context): TravelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TravelDatabase::class.java,
                    "travel_database"
                )
                    .fallbackToDestructiveMigration() // Add this line
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}