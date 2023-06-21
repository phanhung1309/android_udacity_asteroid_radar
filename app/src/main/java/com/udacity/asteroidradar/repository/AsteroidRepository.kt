package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.asAsteroidEntities
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asAsteroidsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {
    private fun getToday(): String {
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    private fun getTomorrow(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getEndWeekDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    val asteroids: LiveData<List<Asteroid>> = database.asteroidDao.getAsteroids().map {
        it.asAsteroidsModel()
    }

    val todayAsteroids: LiveData<List<Asteroid>> =
        database.asteroidDao.getAsteroidsByCloseApproachDate(getToday(), getToday()).map {
            it.asAsteroidsModel()
        }

    val weekAsteroids: LiveData<List<Asteroid>> =
        database.asteroidDao.getAsteroidsByCloseApproachDate(getTomorrow(), getEndWeekDate()).map {
            it.asAsteroidsModel()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val asteroids = AsteroidApi.getAsteroids(getToday(), getEndWeekDate(), Constants.API_KEY)
            database.asteroidDao.insertAsteroids(asteroids.asAsteroidEntities())
            database.asteroidDao.deletePreviousDayAsteroids(getToday())
        }
    }
}