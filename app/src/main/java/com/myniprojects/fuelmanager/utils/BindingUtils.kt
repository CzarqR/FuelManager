package com.myniprojects.fuelmanager.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.model.CarIcon
import java.util.*


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

//@BindingAdapter("refuelingLitres")
//fun TextView.setRefuelingLitres(refueling: Refueling?)
//{
//    refueling?.let {
//        text = refueling.litres.toString()
//    }
//}
//
//@BindingAdapter("refuelingFullPrice")
//fun TextView.setRefuelingFullPrice(refueling: Refueling?)
//{
//    refueling?.let {
//        text = refueling.price.toString()
//    }
//}
//
//@BindingAdapter("refuelingLiterPrice")
//fun TextView.setRefuelingLiterPrice(refueling: Refueling?)
//{
//    refueling?.let {
//
//        text = refueling.price.div(refueling.litres).toString()
//    }
//}
//
//
//@BindingAdapter("refuelingState")
//fun TextView.setRefuelingState(refueling: Refueling?)
//{
//    refueling?.let {
//        text = refueling.previousTankState.toString()
//    }
//}
//
//@BindingAdapter("refuelingOdometerReading")
//fun TextView.setOdometerReading(refueling: Refueling?)
//{
//    refueling?.let {
//        text = refueling.previousOdometerReading.toString()
//    }
//}

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
        if (refueling.comment.isNotEmpty())
        {
            text = refueling.comment
        }
        else
        {
            visibility = View.GONE
        }
    }
}

@BindingAdapter("refuelingDateTimePlace")
fun TextView.setRefuelingDateTimePlace(refueling: Refueling?)
{
    refueling?.let {
        text = "${getDate(
            refueling.dateTimeMillis,
            "dd/MM/yyyy HH:mm:ss",
            Locale.getDefault()
        )} ${refueling.place}"
    }
}



// endregion