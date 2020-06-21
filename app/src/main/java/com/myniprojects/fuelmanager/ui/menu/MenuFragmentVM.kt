package com.myniprojects.fuelmanager.ui.menu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.database.CarDAO
import com.myniprojects.fuelmanager.utils.Log
import com.myniprojects.fuelmanager.utils.formatCars
import kotlinx.coroutines.*

class MenuFragmentVM(
    private val database: CarDAO,
    application: Application
) : AndroidViewModel(application)
{
    private lateinit var selectedCar: Car
    private val cars = database.getAll()
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val carsString = Transformations.map(cars) {
        formatCars(it, application.resources)
    }


    init
    {
        Log.d("VM Create")
    }

    private suspend fun insert(car: Car)
    {
        withContext(Dispatchers.IO) {
            database.insert(car)
        }
    }

    fun addCar()
    {
        uiScope.launch {
            insert(Car())
        }
    }

    fun listAll()
    {
        cars.value!!.forEach {
            Log.d(it)
        }
    }

    override fun onCleared()
    {
        super.onCleared()
        Log.d("VM destroyed")
        viewModelJob.cancel()
    }
}