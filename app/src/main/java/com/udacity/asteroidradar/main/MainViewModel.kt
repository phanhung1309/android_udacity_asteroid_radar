package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.util.FilterAsteroids
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AsteroidDatabase.getInstance(application)
    private val repository = AsteroidRepository(database)

    private val _pictureOfDay = MutableLiveData<PictureOfDay?>()
    val pictureOfDay
        get() = _pictureOfDay

    private val _navigateToAsteroidDetails = MutableLiveData<Asteroid?>()
    val navigateToAsteroidDetails
        get() = _navigateToAsteroidDetails

    private val _filterAsteroids = MutableLiveData<FilterAsteroids>(FilterAsteroids.ALL)

    val asteroids = _filterAsteroids.switchMap {
        when (it!!) {
            FilterAsteroids.WEEK -> repository.weekAsteroids
            FilterAsteroids.TODAY -> repository.todayAsteroids
            else -> repository.asteroids
        }
    }

    init {
        getPictureOfDay()
        getAsteroids()
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                val pictureOfDay = AsteroidApi.getPictureOfDay(Constants.API_KEY)
                _pictureOfDay.value = pictureOfDay

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getAsteroids() {
        viewModelScope.launch {
            try {
                repository.refreshAsteroids()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onAsteroidItemClicked(asteroidData: Asteroid) {
        _navigateToAsteroidDetails.value = asteroidData
    }

    fun onAsteroidDetailsNavigated() {
        _navigateToAsteroidDetails.value = null
    }

    fun onChangeFilter(filter: FilterAsteroids) {
        _filterAsteroids.value = filter
    }
}