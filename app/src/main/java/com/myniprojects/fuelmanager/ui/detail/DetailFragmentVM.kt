package com.myniprojects.fuelmanager.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.database.RefuelingDAO
import com.myniprojects.fuelmanager.utils.Log

class DetailFragmentVM(
    private val database: RefuelingDAO,
    private val refuelingID: Long,
    application: Application
) : AndroidViewModel(application)
{
    val refueling: LiveData<Refueling> = database.get(refuelingID)

    private val _editState: MutableLiveData<Boolean> = MutableLiveData()
    val editState: LiveData<Boolean>
        get() = _editState


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