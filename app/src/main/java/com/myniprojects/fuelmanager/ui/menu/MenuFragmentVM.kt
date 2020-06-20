package com.myniprojects.fuelmanager.ui.menu

import androidx.lifecycle.ViewModel
import com.myniprojects.fuelmanager.utils.Log

class MenuFragmentVM : ViewModel()
{
    init
    {
        Log.d("VM Create")
    }

    override fun onCleared()
    {
        super.onCleared()
        Log.d("VM destroyed")
    }
}