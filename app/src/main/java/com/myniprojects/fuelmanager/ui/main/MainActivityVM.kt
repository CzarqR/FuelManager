package com.myniprojects.fuelmanager.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.utils.Log

class MainActivityVM(application: Application) : AndroidViewModel(application)
{
    val darkTheme: MutableLiveData<Boolean> = MutableLiveData()
    val currency: MutableLiveData<String> = MutableLiveData()
    val volumeUnit: MutableLiveData<String> = MutableLiveData()
    val lengthUnit: MutableLiveData<String> = MutableLiveData()

    init
    {
        Log.d("Main viewModel init")
        loadSettings()
    }

    private fun loadSettings()
    {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication())

        darkTheme.postValue(sharedPref.getBoolean(MainActivity.THEME_KEY, false))

        currency.postValue(
            sharedPref.getString(
                MainActivity.CURRENCY_KEY,
                getApplication<Application>().getString(R.string.currency_default)
            )!!
        )

        volumeUnit.postValue(
            sharedPref.getString(
                MainActivity.VOLUME_UNIT_KEY,
                getApplication<Application>().getString(R.string.volume_unit_default)
            )!!
        )

        lengthUnit.postValue(
            sharedPref.getString(
                MainActivity.LENGTH_UNIT_KEY,
                getApplication<Application>().getString(R.string.length_unit_default)
            )!!
        )
    }

    fun saveSettings(
        newDarkTheme: Boolean,
        newCurrency: String,
        newVolume: String,
        newLength: String
    )
    {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication())

        with(sharedPref.edit()) {
            putString(MainActivity.VOLUME_UNIT_KEY, newVolume)
            putString(MainActivity.CURRENCY_KEY, newCurrency)
            putString(MainActivity.LENGTH_UNIT_KEY, newLength)
            putBoolean(MainActivity.THEME_KEY, newDarkTheme)
            apply()
        }

        darkTheme.postValue(newDarkTheme)
        currency.postValue(newCurrency)
        lengthUnit.postValue(newLength)
        volumeUnit.postValue(newVolume)
    }

}