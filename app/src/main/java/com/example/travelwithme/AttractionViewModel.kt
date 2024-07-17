// AttractionViewModel.kt
package com.example.travelwithme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AttractionViewModel(application: Application) : AndroidViewModel(application) {
    private val database: TravelDatabase by lazy {
        TravelDatabase.getInstance(application.applicationContext)
    }
    private val attractionDao = database.attractionDao()

    val attractions: LiveData<List<Attraction_Data>> = attractionDao.getAllAttractions()

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
    fun getAttractionById(id: Int): LiveData<Attraction_Data?> {
        val result = MutableLiveData<Attraction_Data?>()
        viewModelScope.launch(Dispatchers.IO) {
            result.postValue(attractionDao.getAttractionById(id))
        }
        return result
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
