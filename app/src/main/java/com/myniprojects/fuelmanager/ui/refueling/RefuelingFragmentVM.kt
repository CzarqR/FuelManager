package com.myniprojects.fuelmanager.ui.refueling

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
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
    val refueling = databaseRefueling.getAll(carID)

    val cars = databaseCar.get(carID)

//    private val _car: MutableLiveData<Car> = MutableLiveData()
//    val car: LiveData<Car>
//        get() = _car

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val type: Boolean //true 1 car
        get() = carID.size == 1


    //var carString = formatCars(getCars(), application.applicationContext)

    init
    {
        Log.d("VM car created. CarID: ${carID[0]}")
    }

//    private suspend fun getCarSuspend(carID: Long): Car
//    {
//        return withContext(Dispatchers.IO) {
//            databaseCar.get(carID)
//        }
//    }
//
//    private fun getCar(carID: Long): Car = runBlocking {
//        getCarSuspend(carID)
//    }

//    private fun getCars(): ArrayList<Car>
//    {
//        val list: ArrayList<Car> = ArrayList()
//        carID.forEach {
//            list.add(getCar(it))
//        }
//        return list
//    }

//    private suspend fun getCarSuspend(carID: LongArray): LiveData<List<Car>>
//    {
//        return withContext(Dispatchers.IO) {
//            databaseCar.get(carID)
//        }
//    }
//
//    private fun getCars(carID: LongArray): LiveData<List<Car>> = runBlocking {
//        getCarSuspend(carID)
//    }
//
//    private fun getCars(): ArrayList<Car>
//    {
//        val list: ArrayList<Car> = ArrayList()
//        carID.forEach {
//            list.add(getCar(it))
//        }
//        return list
//    }


    private suspend fun insertRefueling(refueling: Refueling)
    {
        withContext(Dispatchers.IO) {
            databaseRefueling.insert(refueling)
        }
    }

    fun addRefueling(
        litres: Double,
        price: Double,
        state: Byte,
        place: String,
        comment: String,
        odometerReading: Double,
        selectedCar: Int,
        dateTime: Long = -1L
    )
    {
        uiScope.launch {
            insertRefueling(
                Refueling(
                    carID = carID[selectedCar],
                    litres = litres,
                    price = price,
                    previousTankState = state,
                    previousOdometerReading = odometerReading,
                    place = place,
                    comment = comment,
                    dateTimeMillis = if (dateTime == -1L) System.currentTimeMillis() else dateTime
                )
            )
        }
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
        refueling.value!!.forEach {
            Log.d("Car: $it")
        }

        _navigateToRefueling.value = refuelingID
    }

    fun refuelingNavigated()
    {
        _navigateToRefueling.value = null
    }


    // endregion

}