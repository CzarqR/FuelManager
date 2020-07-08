package com.myniprojects.fuelmanager.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.model.CarIcon


// region Menu Fragment

@BindingAdapter("carTitle")
fun TextView.setTitle(car: Car?)
{
    car?.let {
        text = context.resources.getString(R.string.car_title, car.brand, car.model)
    }
}

@BindingAdapter("carEngine")
fun TextView.setEngine(car: Car?)
{
    car?.let {
        text = car.engine
    }
}

@BindingAdapter("carFuel")
fun TextView.setFuel(car: Car?)
{
    car?.let {
        text = car.fuelType
    }
}

@BindingAdapter("carIcon")
fun ImageView.setCarIcon(car: Car?)
{
    car?.let {
        setImageResource(CarIcon.cars[car.iconID.toInt()].icon)
    }
}

// endregion

// region Refueling Fragment

@BindingAdapter("refuelingLitres")
fun TextView.setRefuelingLitres(refueling: Refueling?)
{
    refueling?.let {
        text = refueling.litres.toString()
    }
}

@BindingAdapter("refuelingPrice")
fun TextView.setRefuelingPrice(refueling: Refueling?)
{
    refueling?.let {
        text = refueling.price.toString()
    }
}

@BindingAdapter("refuelingState")
fun TextView.setRefuelingState(refueling: Refueling?)
{
    refueling?.let {
        text = refueling.previousTankState.toString()
    }
}

@BindingAdapter("refuelingOdometerReading")
fun TextView.setOdometerReading(refueling: Refueling?)
{
    refueling?.let {
        text = refueling.previousOdometerReading.toString()
    }
}

@BindingAdapter("refuelingPlace")
fun TextView.setRefuelingPlace(refueling: Refueling?)
{
    refueling?.let {
        text = refueling.place
    }
}

@BindingAdapter("refuelingComment")
fun TextView.setRefuelingComment(refueling: Refueling?)
{
    refueling?.let {
        text = refueling.comment
    }
}

// endregion