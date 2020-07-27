package com.myniprojects.fuelmanager.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.myniprojects.fuelmanager.utils.Log

class MainActivityVM(application: Application) : AndroidViewModel(application)
{
    val currency: MutableLiveData<String> = MutableLiveData()

    init
    {
        Log.d("Main viewmodel init")
    }
}