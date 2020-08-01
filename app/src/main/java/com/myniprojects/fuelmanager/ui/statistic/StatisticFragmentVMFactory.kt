package com.myniprojects.fuelmanager.ui.statistic

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myniprojects.fuelmanager.database.RefuelingDAO

class StatisticFragmentVMFactory(
    private val dataSourceRefueling: RefuelingDAO,
    private val application: Application
) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(StatisticFragmentVM::class.java))
        {
            return StatisticFragmentVM(dataSourceRefueling, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}