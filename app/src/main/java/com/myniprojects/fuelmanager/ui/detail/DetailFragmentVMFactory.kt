package com.myniprojects.fuelmanager.ui.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myniprojects.fuelmanager.database.RefuelingDAO

class DetailFragmentVMFactory(
    private val dataSource: RefuelingDAO,
    private val refuelingID: Long,
    private val application: Application
) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(DetailFragmentVM::class.java))
        {
            return DetailFragmentVM(dataSource, refuelingID, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}