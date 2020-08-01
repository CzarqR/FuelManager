package com.myniprojects.fuelmanager.ui.statistic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.database.RefuelingDAO
import com.myniprojects.fuelmanager.utils.Log
import com.myniprojects.fuelmanager.utils.getMillisFromDate
import kotlinx.coroutines.*

class StatisticFragmentVM(
    private val databaseRefueling: RefuelingDAO,
    application: Application
) : AndroidViewModel(application)
{

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var carID: LongArray? = null

    private var minimumDate: Long = 0
        set(value)
        {
            field = value
            start = value
        }

    private var maximumDate: Long = 0
        set(value)
        {
            field = value
            end = value
        }

    private var start: Long = 0
        set(value)
        {
            field = value
            _startDateSelected.postValue(value)
        }

    private val _startDateSelected: MutableLiveData<Long> = MutableLiveData()
    val startDateSelected: LiveData<Long>
        get() = _startDateSelected

    private var end: Long = 0
        set(value)
        {
            field = value
            _endDateSelected.postValue(value)
        }

    private val _endDateSelected: MutableLiveData<Long> = MutableLiveData()
    val endDateSelected: LiveData<Long>
        get() = _endDateSelected

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
                value.forEach { Log.d(it) }
                minimumDate = value[0].dateTimeMillis
                maximumDate = value[value.lastIndex].dateTimeMillis
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

    private suspend fun getAllRefueling(startDate: Long, endDate: Long): List<Refueling>
    {
        return withContext(Dispatchers.IO) {
            databaseRefueling.getAllNotObservable(startDate, endDate)
        }
    }

    private suspend fun getAllRefueling(
        carID: LongArray,
        startDate: Long,
        endDate: Long
    ): List<Refueling>
    {
        return withContext(Dispatchers.IO) {
            databaseRefueling.getAllNotObservable(carID, startDate, endDate)
        }
    }


    fun loadRefueling(longArray: LongArray?)
    {
        carID = longArray
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

    private fun loadNewRefueling()
    {
        if (carID == null)
        {
            uiScope.launch {
                refueling = getAllRefueling(start, end)
            }
        }
        else
        {
            uiScope.launch {
                refueling = getAllRefueling(carID!!, start, end)
            }
        }
    }

    fun setStartDate(year: Int, month: Int, dayOfMonth: Int)
    {
        start = getMillisFromDate(year, month, dayOfMonth)
        loadNewRefueling()
    }

    fun setEndDate(year: Int, month: Int, dayOfMonth: Int)
    {
        end = getMillisFromDate(year, month, dayOfMonth, 23, 59, 59)
        loadNewRefueling()
    }

}