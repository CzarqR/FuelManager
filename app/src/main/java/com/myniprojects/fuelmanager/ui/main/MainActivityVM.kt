package com.myniprojects.fuelmanager.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.ui.settings.Settings
import com.myniprojects.fuelmanager.utils.Log
import com.myniprojects.fuelmanager.utils.getString


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

        val x = sharedPref.getBoolean(MainActivity.THEME_KEY, false)
        Log.d("Load in fun in VM: $x")

        darkTheme.postValue(sharedPref.getBoolean(MainActivity.THEME_KEY, false))

        currency.postValue(
            sharedPref.getString(
                MainActivity.CURRENCY_KEY,
                getString(R.string.currency_default)
            )!!
        )

        volumeUnit.postValue(
            sharedPref.getString(
                MainActivity.VOLUME_UNIT_KEY,
                getString(R.string.volume_unit_default)
            )!!
        )

        lengthUnit.postValue(
            sharedPref.getString(
                MainActivity.LENGTH_UNIT_KEY,
                getString(R.string.length_unit_default)
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

    fun defaultSettings()
    {
        saveSettings(
            MainActivity.DEFAULT_DARK_THEME,
            getString(R.string.currency_default),
            getString(R.string.volume_unit_default),
            getString(R.string.length_unit_default)
        )
    }

    fun checkChanges(settings: Settings): Boolean
    {
        return getSettings() != settings
    }

    private fun getSettings(): Settings
    {
        return Settings(
            currency.value!!,
            volumeUnit.value!!,
            lengthUnit.value!!,
            darkTheme.value!!

        )
    }


}