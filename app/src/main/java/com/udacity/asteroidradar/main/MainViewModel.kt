package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid

class MainViewModel : ViewModel() {
    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    init {
        mockData()
    }

    private fun mockData() {
        val mockAsteroidList: List<Asteroid> = listOf(
            Asteroid(
                id = 1,
                codename = "AST-001",
                closeApproachDate = "2023-06-01",
                absoluteMagnitude = 4.5,
                estimatedDiameter = 100.0,
                relativeVelocity = 25000.0,
                distanceFromEarth = 500000.0,
                isPotentiallyHazardous = false
            ),
            Asteroid(
                id = 2,
                codename = "AST-002",
                closeApproachDate = "2023-06-02",
                absoluteMagnitude = 3.8,
                estimatedDiameter = 150.0,
                relativeVelocity = 30000.0,
                distanceFromEarth = 450000.0,
                isPotentiallyHazardous = true
            ),
            Asteroid(
                id = 3,
                codename = "AST-003",
                closeApproachDate = "2023-06-03",
                absoluteMagnitude = 5.2,
                estimatedDiameter = 80.0,
                relativeVelocity = 20000.0,
                distanceFromEarth = 600000.0,
                isPotentiallyHazardous = false
            )
        )

        _asteroids.postValue(mockAsteroidList)
    }

    private val _navigateToAsteroidDetails = MutableLiveData<Asteroid?>()
    val navigateToAsteroidDetails
        get() = _navigateToAsteroidDetails

    fun onAsteroidItemClicked(asteroidData: Asteroid) {
        _navigateToAsteroidDetails.value = asteroidData
    }

    fun onAsteroidDetailsNavigated() {
        _navigateToAsteroidDetails.value = null
    }
}