package com.myniprojects.fuelmanager.ui.menu

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myniprojects.fuelmanager.database.CarDAO

class MenuFragmentVMFactory(
    private val dataSource: CarDAO,
    private val application: Application
) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(MenuFragmentVM::class.java))
        {
            return MenuFragmentVM(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}