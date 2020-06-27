package com.myniprojects.fuelmanager.ui.car

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.database.CarDAO
import com.myniprojects.fuelmanager.utils.Log
import com.myniprojects.fuelmanager.utils.formatCars
import kotlinx.coroutines.*

class CarFragmentVM(
    private val database: CarDAO,
    application: Application
) : AndroidViewModel(application)
{
    val cars = database.getAll()
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val carsString = Transformations.map(cars) {
        formatCars(it, application.resources)
    }


    init
    {
        Log.d("VM menu Create")
    }

    private suspend fun insert(car: Car)
    {
        withContext(Dispatchers.IO) {
            database.insert(car)
        }
    }

    fun addCar(brand: String, model: String, engine: String, fuelType: String, iconID: Byte)
    {
        uiScope.launch {
            insert(
                Car(
                    brand = brand,
                    model = model,
                    engine = engine,
                    fuelType = fuelType,
                    iconID = iconID
                )
            )
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

    private val _navigateToCar = MutableLiveData<Long>()
    val navigateToCar
        get() = _navigateToCar

    fun carClicked(carID: Long)
    {
        _navigateToCar.value = carID
    }

    fun carNavigated()
    {
        _navigateToCar.value = null
    }

}