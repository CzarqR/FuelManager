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

    private suspend fun get(carID: Long): Car
    {
        return withContext(Dispatchers.IO) {
            database.get(carID)
        }
    }

    private suspend fun update(car: Car)
    {
        withContext(Dispatchers.IO) {
            database.update(car)
        }
    }

    private suspend fun insert(car: Car)
    {
        withContext(Dispatchers.IO) {
            database.insert(car)
        }
    }

    private suspend fun delete(carID: Long)
    {
        withContext(Dispatchers.IO) {
            database.delete(carID)
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
        Log.d("VM Car destroyed")
        viewModelJob.cancel()
    }

    // region navigation click

    private val _navigateToCar = MutableLiveData<Long>()
    val navigateToCar
        get() = _navigateToCar

    fun carClicked(carID: Long)
    {
        _navigateToCar.value = carID
    }

    fun deleteCar(carID: Long)
    {
        Log.d("Delete car with id $carID")
        uiScope.launch {
            delete(carID)
        }
    }

    fun carNavigated()
    {
        _navigateToCar.value = null
    }

    fun editCar(
        brand: String,
        model: String,
        engine: String,
        fuelType: String,
        iconID: Byte,
        carID: Long
    )
    {
        uiScope.launch {
            update(
                Car(
                    carID = carID,
                    brand = brand,
                    model = model,
                    engine = engine,
                    fuelType = fuelType,
                    iconID = iconID
                )
            )
        }
    }


    fun getCar(carID: Long): Car = runBlocking {
        get(carID)
    }

    // endregion


}