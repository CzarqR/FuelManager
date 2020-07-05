package com.myniprojects.fuelmanager.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.database.RefuelingDAO
import com.myniprojects.fuelmanager.utils.Log
import kotlinx.coroutines.*

class DetailFragmentVM(
    private val database: RefuelingDAO,
    private val refuelingID: Long,
    application: Application
) : AndroidViewModel(application)
{
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val refueling: LiveData<Refueling> = database.get(refuelingID)

    private val _editState: MutableLiveData<Boolean> = MutableLiveData()
    val editState: LiveData<Boolean>
        get() = _editState

    private suspend fun update(refueling: Refueling)
    {
        withContext(Dispatchers.IO) {
            database.update(refueling)
        }
    }

    private suspend fun delete(refuelingID: Long)
    {
        withContext(Dispatchers.IO) {
            database.delete(refuelingID)
        }
    }

    fun editRefueling(
        litres: Double,
        price: Double,
        previousState: Byte,
        place: String,
        comment: String
    )
    {
        uiScope.launch {
            update(
                Refueling(
                    refuelingID,
                    refueling.value!!.carID,
                    litres,
                    price,
                    place,
                    previousState,
                    comment
                )
            )
        }
    }


    override fun onCleared()
    {
        super.onCleared()
        Log.d("VM Detail destroyed")
        viewModelJob.cancel()
    }

    init
    {
        Log.d("Detail VM create")
        _editState.value = false
    }

    fun changeState()
    {
        _editState.value = !_editState.value!!
    }


}