package com.myniprojects.fuelmanager.utils

import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.myniprojects.fuelmanager.database.Car

fun formatCars(cars: List<Car>, resources: Resources): Spanned
{
    //TODO correct format
    val sb = StringBuilder()
    sb.apply {
        cars.forEach {
            append(it.carID.toString() + " - ")
            append(it.brand + " ")
            append(it.model + ". ")
            append(it.engine + ", ")
            append(it.fuelType)
            append(". Icon: ${it.iconID}")
            append("<br>")
        }
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
    {
        Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    }
    else
    {
        HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}
