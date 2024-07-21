package com.example.travelwithme

import com.example.travelwithme.R

object CategoryIcons {
    private val categoryIconMap = mapOf(
        "Beach" to R.drawable.icon_beach,
        "Museum" to R.drawable.icon_museum,
        "Park" to R.drawable.icon_park,
        "Shopping" to R.drawable.icon_shopping,
        "Night Life" to R.drawable.icon_nightlife,
        "Restaurant" to R.drawable.icon_restaurant
    )

    fun getIconForCategory(category: String): Int {
        return categoryIconMap[category] ?: R.drawable.icon_all
    }
}
