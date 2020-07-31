package com.myniprojects.fuelmanager.ui.statistic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.myniprojects.fuelmanager.database.CarDAO
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.database.RefuelingDAO
import com.myniprojects.fuelmanager.utils.Log
import kotlinx.coroutines.*

class StatisticFragmentVM(
    private val databaseRefueling: RefuelingDAO,
    private val databaseCar: CarDAO,
    application: Application
) : AndroidViewModel(application)
{

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var startDate: Long = 0
    private var endDate: Long = 0

    private var refueling: List<Refueling>? = null
        set(value)
        {
            field = value
            if (value == null)
            {
                Log.d("Refueling list is null")
            }
            else
            {
                value.forEach {
                    Log.d(it)
                }
                startDate = value[0].dateTimeMillis
                endDate = value[value.lastIndex].dateTimeMillis
                Log.d(startDate)
                Log.d(endDate)
            }
        }

    private suspend fun getAllRefueling(carID: LongArray): List<Refueling>
    {
        return withContext(Dispatchers.IO) {
            databaseRefueling.getAllNotObservable(carID)
        }
    }

    private suspend fun getAllRefueling(): List<Refueling>
    {
        return withContext(Dispatchers.IO) {
            databaseRefueling.getAllNotObservable()
        }
    }

    fun loadRefueling(longArray: LongArray?)
    {
        if (longArray != null)
        {
            uiScope.launch {
                refueling = getAllRefueling(longArray)
            }
        }
        else
        {
            uiScope.launch {
                refueling = getAllRefueling()
            }
        }
    }


    init
    {
        Log.d("Statistic VM created")
    }
}