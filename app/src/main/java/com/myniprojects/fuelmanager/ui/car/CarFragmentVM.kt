package com.myniprojects.fuelmanager.ui.car

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.myniprojects.fuelmanager.database.CarDAO
import com.myniprojects.fuelmanager.utils.Log

class CarFragmentVM(
    private val database: CarDAO,
    private val carID: Long,
    application: Application
) :
        AndroidViewModel(application)
{
    init
    {
        Log.d("VM car created. CarID: $carID")
    }
}