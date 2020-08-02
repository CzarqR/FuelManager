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
            Log.d("start setter $value")
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
            if (value != null)
            {
                if (isInit) //opening fragment
                {
                    if (value.isNotEmpty()) // selected cars have refueling
                    {
                        _canShowStatistic.postValue(true)
                        minimumDate = value[0].dateTimeMillis
                        maximumDate = value[value.lastIndex].dateTimeMillis
                        calculateData()
                    }
                    else // selected cars don't have refueling
                    {
                        _canShowStatistic.postValue(false)
                    }
                    isInit = !isInit
                }
                else // changing date
                {
                    if (value.isNotEmpty()) // selected cars have refueling
                    {
                        _isFuelInDateRange.postValue(true)
                        calculateData()
                    }
                    else // selected cars don't have refueling
                    {
                        _isFuelInDateRange.postValue(false)
                    }
                }

            }

        }

    private var _canShowStatistic: MutableLiveData<Boolean> = MutableLiveData()
    val canShowStatistic: LiveData<Boolean>
        get() = _canShowStatistic

    private var _isFuelInDateRange: MutableLiveData<Boolean> = MutableLiveData()
    val isFuelInDateRange: LiveData<Boolean>
        get() = _isFuelInDateRange


    private fun calculateData()
    {
        if (refueling != null)
        {
            _numbersOfRefueling.postValue(refueling!!.size)

            var sumPrice = 0.0

            refueling!!.forEach {
                sumPrice += it.price
            }

            _totalPrice.postValue(sumPrice)
        }

    }


    // region observable stats

    private var _numbersOfRefueling: MutableLiveData<Int> = MutableLiveData()
    val numbersOfRefueling: LiveData<Int>
        get() = _numbersOfRefueling

    private var _totalPrice: MutableLiveData<Double> = MutableLiveData()
    val totalPrice: LiveData<Double>
        get() = _totalPrice

    // endregion


    private var isInit: Boolean = true

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
        isInit = true
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