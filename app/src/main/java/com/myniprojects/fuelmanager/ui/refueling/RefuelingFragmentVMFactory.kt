package com.myniprojects.fuelmanager.ui.refueling

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myniprojects.fuelmanager.database.CarDAO
import com.myniprojects.fuelmanager.database.RefuelingDAO

class RefuelingFragmentVMFactory(
    private val dataSourceRefueling: RefuelingDAO,
    private val dataSourceCar: CarDAO,
    private val carID: LongArray,
    private val application: Application
) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(RefuelingFragmentVM::class.java))
        {
            return RefuelingFragmentVM(dataSourceRefueling, dataSourceCar, carID, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}