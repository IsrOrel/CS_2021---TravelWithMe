// TravelDatabase.kt
package com.example.travelwithme.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Attraction_Data::class, User_Data::class], version = 1, exportSchema = false)

@TypeConverters(User_Data_Convertors::class)
abstract class TravelDatabase : RoomDatabase() {

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
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
