package com.example.travelwithme.Data

import android.content.Context
import android.util.Log
import com.example.travelwithme.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataSeeder(private val context: Context) {

    private val database = TravelDatabase.getInstance(context)

    fun seedData() {
        CoroutineScope(Dispatchers.IO).launch {
            val attractionsDao = database.attractionDao()

            // Define attractions for each city
            val attractions = listOf(
                // London
                Attraction_Data(
                    id = 0,
                    image = R.drawable.icon_beach,
                    title = "Hyde Park",
                    description = "A large park in central London.",
                    city = "London",
                    category = "Park",
                    address = "Hyde Park, London"
                ),
                Attraction_Data(
                    id = 0,
                    image = R.drawable.icon_museum,
                    title = "British Museum",
                    description = "A museum dedicated to human history.",
                    city = "London",
                    category = "Museum",
                    address = "Great Russell St, London"
                ),
                // Rome
                Attraction_Data(
                    id = 0,
                    image = R.drawable.icon_museum,
                    title = "Colosseum",
                    description = "An ancient amphitheater in Rome.",
                    city = "Rome",
                    category = "Museums",
                    address = "Piazza del Colosseo, 1, 00184 Roma RM, Italy"
                ),
                Attraction_Data(
                    id = 0,
                    image = R.drawable.icon_museum,
                    title = "Vatican Museums",
                    description = "A group of art and Christian museums situated within Vatican City.",
                    city = "Rome",
                    category = "Museums",
                    address = "Viale Vaticano, 00165 Roma RM, Italy"
                ),
                // Amsterdam
                Attraction_Data(
                    id = 0,
                    image = R.drawable.icon_beach,
                    title = "Vondelpark",
                    description = "A large public urban park in Amsterdam.",
                    city = "Amsterdam",
                    category = "Park",
                    address = "Vondelpark, Amsterdam"
                ),
                Attraction_Data(
                    id = 0,
                    image = R.drawable.icon_museum,
                    title = "Rijksmuseum",
                    description = "A Dutch national museum dedicated to arts and history.",
                    city = "Amsterdam",
                    category = "Museum",
                    address = "Museumstraat 1, Amsterdam"
                )
                // Add more attractions here
            )

            // Insert attractions into the database
            for (attraction in attractions) {
                val existingCount = attractionsDao.countAttraction(
                    attraction.title,
                    attraction.city,
                    attraction.address
                )
                if (existingCount == 0) {
                    attractionsDao.insertAttraction(attraction)
                }
            }
            for (attraction in attractions) {
                val existingAttraction = attractionsDao.getAttractionByTitleAndCity(attraction.title, attraction.city)
                if (existingAttraction == null) {
                    attractionsDao.insertAttraction(attraction)
                }
            }

            // Log the counts after inserting data
            // This code is only for demonstration. In a real app, you would observe LiveData in a lifecycle-aware component.
            CoroutineScope(Dispatchers.Main).launch {
                val londonAttractions = attractionsDao.getAttractionsForCity("London").value?.size ?: 0
                val romeAttractions = attractionsDao.getAttractionsForCity("Rome").value?.size ?: 0
                val amsterdamAttractions = attractionsDao.getAttractionsForCity("Amsterdam").value?.size ?: 0

                Log.d("DataSeeder", "London Attractions count: $londonAttractions")
                Log.d("DataSeeder", "Rome Attractions count: $romeAttractions")
                Log.d("DataSeeder", "Amsterdam Attractions count: $amsterdamAttractions")
            }
        }
    }
}
