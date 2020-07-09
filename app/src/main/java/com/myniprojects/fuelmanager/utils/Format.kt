package com.myniprojects.fuelmanager.utils

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car

fun formatCars(cars: List<Car>, context: Context): Spanned
{
    val sb = StringBuilder()

    val carColors = context.resources.getIntArray(R.array.car_colors)

    sb.apply {

        for ((index, value) in cars.withIndex())
        {

            Log.d("${carColors[index]} Color")
            append(
                "<span style=\"color:${carColors[index]}\"> ${context.getString(
                    R.string.car_title,
                    value.brand,
                    value.model
                )} </span> <br> "
            )
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
