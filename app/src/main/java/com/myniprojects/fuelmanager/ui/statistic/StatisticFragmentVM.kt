package com.myniprojects.fuelmanager.ui.statistic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.myniprojects.fuelmanager.database.CarDAO
import com.myniprojects.fuelmanager.database.RefuelingDAO
import com.myniprojects.fuelmanager.utils.Log

class StatisticFragmentVM(
    private val databaseRefueling: RefuelingDAO,
    private val databaseCar: CarDAO,
    application: Application
) : AndroidViewModel(application)
{
    val cars = databaseCar.getAll()

    init
    {
        Log.d("Statistic VM created")
    }
}