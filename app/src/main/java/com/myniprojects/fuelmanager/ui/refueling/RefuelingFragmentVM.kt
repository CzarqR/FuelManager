package com.myniprojects.fuelmanager.ui.refueling

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.database.CarDAO
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.database.RefuelingDAO
import com.myniprojects.fuelmanager.utils.Log
import kotlinx.coroutines.*

class RefuelingFragmentVM(
    private val databaseRefueling: RefuelingDAO,
    private val databaseCar: CarDAO,
    private val carID: LongArray,
    application: Application
) :
        AndroidViewModel(application)
{
    val refueling = databaseRefueling.getAll(carID[0]) //todo

    private val _car: MutableLiveData<Car> = MutableLiveData()
    val car: LiveData<Car>
        get() = _car

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private suspend fun getCarSuspend(carID: Long): Car
    {
        return withContext(Dispatchers.IO) {
            databaseCar.get(carID)
        }
    }

    private fun getCar(carID: Long): Car = runBlocking {
        getCarSuspend(carID)
    }

    private suspend fun insertRefueling(refueling: Refueling)
    {
        withContext(Dispatchers.IO) {
            databaseRefueling.insert(refueling)
        }
    }

    fun addRefueling(litres: Double, price: Double, state: Byte, place: String, comment: String)
    {
        uiScope.launch {
            insertRefueling(
                Refueling(
                    carID = carID[0],
                    litres = litres,
                    price = price,
                    previousState = state,
                    place = place,
                    comment = comment
                )
            )
        }
    }


    init
    {
        Log.d("VM car created. CarID: ${carID[0]}")
        _car.value = getCar(carID[0])
        Log.d(_car.value!!)
    }

    override fun onCleared()
    {
        super.onCleared()
        Log.d("VM refueling destroyed")
        viewModelJob.cancel()
    }

    // region navigation

    private val _navigateToRefueling = MutableLiveData<Long>()
    val navigateToRefueling
        get() = _navigateToRefueling

    fun refuelingClicked(refuelingID: Long)
    {
        _navigateToRefueling.value = refuelingID
    }

    fun refuelingNavigated()
    {
        _navigateToRefueling.value = null
    }

    // endregion

}