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
    private val _refueling: MutableLiveData<Refueling> = MutableLiveData()
    val refueling: LiveData<Refueling>
        get() = _refueling

    init
    {
        Log.d("Detail VM create")
    }
}