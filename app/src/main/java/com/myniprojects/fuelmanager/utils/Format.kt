package com.myniprojects.fuelmanager.utils

import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car

fun formatCars(cars: List<Car>, resources: Resources): Spanned
{
    val sb = StringBuilder()
    sb.apply {
        cars.forEach {
            append("<b> ${resources.getString(R.string.car_title, it.brand, it.model)} </b> | ")
        }
    }
    sb.replace(sb.length - 3, sb.length - 1, "")
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
    {
        Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    }
    else
    {
        HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}
