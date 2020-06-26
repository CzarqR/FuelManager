package com.myniprojects.fuelmanager.ui.car

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.database.RefuelingDAO
import com.myniprojects.fuelmanager.utils.Log
import kotlinx.coroutines.*

class CarFragmentVM(
    private val database: RefuelingDAO,
    private val carID: Long,
    application: Application
) :
        AndroidViewModel(application)
{
    val refueling = database.getAll()
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private suspend fun insert(refueling: Refueling)
    {
        withContext(Dispatchers.IO) {
            database.insert(refueling)
        }
    }

    fun addRefueling()
    {
        val ref = Refueling(
            carID = carID,
            litres = 12.4,
            price = 13.5
        )
        uiScope.launch {
            insert(ref)
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
}