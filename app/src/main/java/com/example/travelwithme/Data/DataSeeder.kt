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
                    image = R.drawable.icon_park,
                    title = "הייד פארק",
                    description = "פארק גדול באמצע לונדון",
                    city = "לונדון",
                    category = "פארקים",
                    address = "הייד פארק, לונדון"
                ),
                Attraction_Data(
                    image = R.drawable.icon_museum,
                    title = "המוזיאון הבריטי",
                    description = "מוזיאון המוקדש להיסטורית האדם.",
                    city = "לונדון",
                    category = "מוזיאונים",
                    address = "רחוב ראסל הגדול, לונדון"
                ),
                Attraction_Data(
                    image = R.drawable.icon_restaurant,
                    title = "דישום",
                    description = "מסעדה המציעה אוכל הודי מעולה.",
                    city = "לונדון",
                    category = "מסעדות",
                    address = "12 Upper St, Islington, London N1 0PQ, UK"
                ),
                Attraction_Data(
                    image = R.drawable.icon_park,
                    title = "גריניץ' פארק",
                    description = "פארק היסטורי עם תצפית על העיר.",
                    city = "לונדון",
                    category = "פארקים",
                    address = "Greenwich Park, London SE10 8QY, UK"
                ),
                Attraction_Data(
                    image = R.drawable.icon_museum,
                    title = "הגלריה הלאומית",
                    description = "מוזיאון אומנות עם יצירות מהמאות ה-13 ועד ה-19.",
                    city = "לונדון",
                    category = "מוזיאונים",
                    address = "Trafalgar Square, London WC2N 5DN, UK"
                ),
                Attraction_Data(
                    image = R.drawable.icon_shopping,
                    title = "אוקספורד סטריט",
                    description = "רחוב הקניות המרכזי בלונדון.",
                    city = "לונדון",
                    category = "קניות",
                    address = "Oxford Street, London, UK"
                ),
                        // Rome
                Attraction_Data(
                    image = R.drawable.icon_museum,
                    title = "קולוסיאום",
                    description = "אמפיתיאטרון עתיק ברומא.",
                    city = "רומא",
                    category = "מוזיאונים",
                    address = "פיאצה דל קולוסיאו, 1, 00184 רומא RM, איטליה"
                ),
                Attraction_Data(
                    image = R.drawable.icon_museum,
                    title = "מוזיאוני הוותיקן",
                    description = "קבוצת מוזיאונים לאמנות ונצרות הנמצאת בתוך עיר הוותיקן.",
                    city = "רומא",
                    category = "מוזיאונים",
                    address = "וויילה ואטיקנו, 00165 רומא RM, איטליה"
                ),
                Attraction_Data(
                    image = R.drawable.icon_restaurant,
                    title = "לה פרגולה",
                    description = "מסעדת גורמה עם כוכב מישלן.",
                    city = "רומא",
                    category = "מסעדות",
                    address = "Via Alberto Cadlolo, 101, 00136 Roma RM, Italy"
                ),
                Attraction_Data(
                    image = R.drawable.icon_park,
                    title = "וילה בורגהנסה",
                    description = "פארק רחב ידיים עם גן חיות ומוזיאונים.",
                    city = "רומא",
                    category = "פארקים",
                    address = "Piazza di Siena, 00197 Roma RM, Italy"
                ),
                Attraction_Data(
                    image = R.drawable.icon_museum,
                    title = "גלריה בורגזה",
                    description = "מוזיאון לאומנות עם יצירות מופת מהתקופה הרומית והרנסנס.",
                    city = "רומא",
                    category = "מוזיאונים",
                    address = "Piazzale Scipione Borghese, 5, 00197 Roma RM, Italy"
                ),
                Attraction_Data(
                    image = R.drawable.icon_shopping,
                    title = "ויה דל קורסו",
                    description = "רחוב קניות פופולרי במרכז רומא.",
                    city = "רומא",
                    category = "קניות",
                    address = "Via del Corso, Roma RM, Italy"
                ),
                //Amsterdam
                        Attraction_Data(
                    image = R.drawable.icon_park,
                    title = "פונדלפארק",
                    description = "פארק ציבורי גדול בעיר אמסטרדם.",
                    city = "אמסטרדם",
                    category = "פארקים",
                    address = "פונדלפארק, אמסטרדם"
                ),
                Attraction_Data(
                    image = R.drawable.icon_museum,
                    title = "רייקסמוזיאום",
                    description = "מוזיאון לאומי הולנדי המוקדש לאמנויות ולהיסטוריה.",
                    city = "אמסטרדם",
                    category = "מוזיאונים",
                    address = "מוזיאוםסטרט 1, אמסטרדם"
                ),
                Attraction_Data(
                    image = R.drawable.icon_restaurant,
                    title = "דה קאס",
                    description = "מסעדת שף עם גינה אורגנית.",
                    city = "אמסטרדם",
                    category = "מסעדות",
                    address = "Kamerlingh Onneslaan 3, 1097 DE Amsterdam, Netherlands"
                ),
                Attraction_Data(
                    image = R.drawable.icon_park,
                    title = "וונדלפארק",
                    description = "פארק פופולרי עם אגמים ומסעדות.",
                    city = "אמסטרדם",
                    category = "פארקים",
                    address = "Vondelpark, Amsterdam, Netherlands"
                ),
                Attraction_Data(
                    image = R.drawable.icon_museum,
                    title = "מוזיאון רייקס",
                    description = "מוזיאון לאומנות עם אוסף גדול מההיסטוריה ההולנדית.",
                    city = "אמסטרדם",
                    category = "מוזיאונים",
                    address = "Museumstraat 1, 1071 XX Amsterdam, Netherlands"
                ),
                Attraction_Data(
                    image = R.drawable.icon_shopping,
                    title = "דה נגן דטראטג'יס",
                    description = "אזור קניות ייחודי עם חנויות בוטיק.",
                    city = "אמסטרדם",
                    category = "קניות",
                    address = "De Negen Straatjes, Amsterdam, Netherlands"
                ),

            )

            // Insert attractions into the database
            for (attraction in attractions) {
                    attractionsDao.insertAttraction(attraction)
                }

            // Log the counts after inserting data
            // This code is only for demonstration. In a real app, you would observe LiveData in a lifecycle-aware component.
            CoroutineScope(Dispatchers.Main).launch {
                val londonAttractions = attractionsDao.getAttractionsForCity("לונדון").value?.size ?: 0
                val romeAttractions = attractionsDao.getAttractionsForCity("רומא").value?.size ?: 0
                val amsterdamAttractions = attractionsDao.getAttractionsForCity("אמסטרדם").value?.size ?: 0

                Log.d("DataSeeder", "London Attractions count: $londonAttractions")
                Log.d("DataSeeder", "Rome Attractions count: $romeAttractions")
                Log.d("DataSeeder", "Amsterdam Attractions count: $amsterdamAttractions")
            }
        }
    }
}
