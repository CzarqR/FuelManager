package com.myniprojects.fuelmanager.ui.detail

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.database.RefuelingDAO
import com.myniprojects.fuelmanager.ui.main.MainActivity
import com.myniprojects.fuelmanager.utils.Log
import com.myniprojects.fuelmanager.utils.getString
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

    val shareMessage: String
        get()
        {
            val tmp = refueling.value
            return if (tmp != null)
            {
                getApplication<Application>().getString(
                    R.string.share_message,
                    tmp.place,
                    tmp.price.toString(),
                    MainActivity.currency
                )
            }
            else
            {
                getString(
                    R.string.share_message_no_input
                )
            }
        }


    private suspend fun delete()
    {
        withContext(Dispatchers.IO) {
            database.delete(refuelingID)
        }
    }

    fun deleteRefueling()
    {
        uiScope.launch {
            delete()
        }
    }

    @StringRes
    fun editRefueling(
        litres: String,
        price: String,
        tankState: String,
        place: String,
        odometerReading: String,
        comment: String
    ): Int
    {
        val result = Refueling.validateData(litres, price, tankState, odometerReading)

        if (result == R.string.success_code)
        {
            uiScope.launch {
                update(
                    Refueling(
                        refuelingID,
                        refueling.value!!.carID,
                        litres.toDouble(),
                        price.toDouble(),
                        place,
                        tankState.toByte(),
                        odometerReading.toDouble(),
                        comment,
                        refueling.value!!.dateTimeMillis
                    )
                )
            }
            return R.string.refueling_edited
        }

        return result
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

    @StringRes
    fun canEditRefueling(
        litres: String,
        price: String,
        tankState: String,
        odometerReading: String
    ): Int
    {
        val result = Refueling.validateData(litres, price, tankState, odometerReading)

        if (result == R.string.success_code)
        {
            return R.string.refueling_edited
        }

        return result
    }


}