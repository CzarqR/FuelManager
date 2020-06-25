package com.myniprojects.fuelmanager.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.model.CarIcon

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
