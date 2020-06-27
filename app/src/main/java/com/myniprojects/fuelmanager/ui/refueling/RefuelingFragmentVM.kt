package com.myniprojects.fuelmanager.ui.refueling

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.database.RefuelingDAO
import com.myniprojects.fuelmanager.utils.Log
import kotlinx.coroutines.*

class RefuelingFragmentVM(
    private val database: RefuelingDAO,
    private val carID: Long,
    application: Application
) :
        AndroidViewModel(application)
{
    val refueling = database.getAll(carID)
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private suspend fun insert(refueling: Refueling)
    {
        withContext(Dispatchers.IO) {
            database.insert(refueling)
        }
    }

    fun addRefueling(litres: Double, price: Double, state: Byte, place: String, comment: String)
    {
        uiScope.launch {
            insert(
                Refueling(
                    carID = carID,
                    litres = litres,
                    price = price,
                    previousState = state,
                    place = place,
                    comment = comment
                )
            )
        }
    }

    fun listAll()
    {
        refueling.value!!.forEach {
            Log.d(it)
        }
    }


    init
    {
        Log.d("VM car created. CarID: $carID")
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