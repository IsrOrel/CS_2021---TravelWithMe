package com.example.travelwithme.Data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AttractionViewModel(application: Application) : AndroidViewModel(application) {
    private val database: TravelDatabase by lazy {
        TravelDatabase.getInstance(application.applicationContext)
    }
    private val attractionDao = database.attractionDao()

    fun getAllAttractionsForCity(city: String): LiveData<List<Attraction_Data>> {
        return attractionDao.getAttractionsForCity(city)
    }

    fun getAttractionsForCityAndCategory(city: String, category: String): LiveData<List<Attraction_Data>> {
        return attractionDao.getAttractionsForCityAndCategory(city, category)
    }

    fun insertAttraction(attraction: Attraction_Data) {
        viewModelScope.launch(Dispatchers.IO) {
            attractionDao.insertAttraction(attraction)
        }
    }

    fun insertAttractions(attractions: List<Attraction_Data>) {
        viewModelScope.launch(Dispatchers.IO) {
            attractionDao.insertAttractions(attractions)
        }
    }

    fun updateAttraction(attraction: Attraction_Data) {
        viewModelScope.launch(Dispatchers.IO) {
            attractionDao.updateAttraction(attraction)
        }
    }

    fun deleteAttraction(attraction: Attraction_Data) {
        viewModelScope.launch(Dispatchers.IO) {
            attractionDao.deleteAttraction(attraction)
        }
    }
}
