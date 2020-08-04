package com.myniprojects.fuelmanager.ui.car

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.database.CarDAO
import com.myniprojects.fuelmanager.utils.Log
import kotlinx.coroutines.*

class CarFragmentVM(
    private val database: CarDAO,
    application: Application
) : AndroidViewModel(application)
{
    val cars = database.getAll()
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


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


    @StringRes
    fun addCar(
        brand: String,
        model: String,
        engine: String,
        fuelType: String,
        tankSize: String,
        iconID: Byte
    ): Int
    {
        val result = Car.validateData(
            brand, model, tankSize
        )

        if (result == R.string.succes_code)
        {
            uiScope.launch {
                insert(
                    Car(
                        brand = brand,
                        model = model,
                        engine = engine,
                        fuelType = fuelType,
                        tankSize = tankSize.toDouble(),
                        iconID = iconID
                    )
                )
            }
            return R.string.car_added
        }
        return result
    }




    override fun onCleared()
    {
        super.onCleared()
        Log.d("VM Car destroyed")
        viewModelJob.cancel()
    }

// region navigation click

    private val _navigateToRefueling = MutableLiveData<LongArray>()
    val navigateToRefueling: LiveData<LongArray>
        get() = _navigateToRefueling

    fun carClicked(carID: LongArray)
    {
        _navigateToRefueling.value = carID
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
        _navigateToRefueling.value = null
    }

    @StringRes
    fun editCar(
        brand: String,
        model: String,
        engine: String,
        fuelType: String,
        iconID: Byte,
        tankSize: String,
        carID: Long
    ): Int
    {
        val result = Car.validateData(brand, model, tankSize)

        if (result == R.string.succes_code)
        {
            uiScope.launch {
                update(
                    Car(
                        carID = carID,
                        brand = brand,
                        model = model,
                        engine = engine,
                        fuelType = fuelType,
                        tankSize = tankSize.toDouble(),
                        iconID = iconID
                    )
                )
            }

            return R.string.car_edited
        }
        return result
    }


    fun getCar(carID: Long): Car = runBlocking {
        get(carID)
    }


// endregion


}