package com.myniprojects.fuelmanager.ui.car

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myniprojects.fuelmanager.database.RefuelingDAO

class CarFragmentVMFactory(
    private val dataSource: RefuelingDAO,
    private val carID: Long,
    private val application: Application
) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(CarFragmentVM::class.java))
        {
            return CarFragmentVM(dataSource, carID, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}