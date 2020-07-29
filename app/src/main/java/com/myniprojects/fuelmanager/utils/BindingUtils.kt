package com.myniprojects.fuelmanager.utils

import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.model.CarIcon
import com.myniprojects.fuelmanager.ui.main.MainActivity


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

@BindingAdapter("refuelingCost")
fun TextView.setRefuelingCost(refueling: Refueling?)
{
    refueling?.let {
        text = SpanFormatter.format(
            this.context.getText(R.string.litres_format),
            refueling.litres.toStringFormatted(),
            refueling.price.toStringFormatted(),
            (refueling.litres * refueling.price).round(2).toStringFormatted(),
            MainActivity.volumeUnit,
            MainActivity.currency
        )
    }
}


@BindingAdapter(value = ["refuelingPlace", "indexPlace"], requireAll = true)
fun TextView.setRefuelingPlace(refueling: Refueling?, index: Int)
{
    refueling?.let {
        text = if (refueling.place.isNotEmpty())
        {
            SpannableStringBuilder(
                SpanFormatter.format(
                    this.context.getText(R.string.date_place_format),
                    getDate(
                        refueling.dateTimeMillis
                    ),
                    refueling.place
                )
            )
        }
        else
        {
            SpannableStringBuilder(
                SpanFormatter.format(
                    this.context.getText(R.string.date_format),
                    getDate(
                        refueling.dateTimeMillis
                    )
                )
            )
        }.setSpan(ForegroundColorSpan(context.resources.getIntArray(R.array.car_colors)[index]))


    }
}

@BindingAdapter("refuelingData")
fun TextView.setRefuelingData(refueling: Refueling?)
{
    refueling?.let {

        text = this.context.getString(
            R.string.odometer_reading_format,
            refueling.previousOdometerReading.toStringFormatted(),
            refueling.previousTankState,
            MainActivity.lengthUnit
        )
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


//endregion