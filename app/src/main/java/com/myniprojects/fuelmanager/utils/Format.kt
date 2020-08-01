package com.myniprojects.fuelmanager.utils

import android.content.Context
import android.os.Build
import android.text.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun formatCars(cars: List<Car>, context: Context): Spanned
{
    val sb = StringBuilder()

    val carColors = context.resources.getIntArray(R.array.car_colors)

    sb.apply {

        for ((index, value) in cars.withIndex())
        {
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

fun getCarNamesFormatted(cars: List<Car>, context: Context): ArrayList<Spanned>
{

    val carColors = context.resources.getIntArray(R.array.car_colors)

    val carNames = ArrayList<Spanned>()

    for ((index, value) in cars.withIndex())
    {
        val s = "<span style=\"color:${carColors[index]}\"> ${context.getString(
            R.string.car_title,
            value.brand,
            value.model
        )} </span>"

        carNames.add(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY)
            }
            else
            {
                HtmlCompat.fromHtml(s, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        )
    }

    return carNames
}

@Suppress("unused")
fun getCarNamesSimple(cars: List<Car>, context: Context): ArrayList<Spanned>
{
    val carNames = ArrayList<Spanned>()

    for (value in cars)
    {
        carNames.add(
            SpannedString(
                context.getString(
                    R.string.car_title,
                    value.brand,
                    value.model
                )
            )
        )
    }

    carNames.add(SpannedString(context.getString(R.string.all_cars)))

    return carNames
}


const val FULL_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss"
const val DATE_FORMAT = "dd/MM/yyyy"

fun getDate(milliSeconds: Long, dateFormat: String, locale: Locale): String
{
    Log.d("Millis in fun $milliSeconds")

    val formatter = SimpleDateFormat(dateFormat, locale)

    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = milliSeconds

    return formatter.format(calendar.time)
}

fun getDate(milliSeconds: Long, dateFormat: String): String
{
    return getDate(milliSeconds, dateFormat, Locale.getDefault())
}

fun getDate(milliSeconds: Long): String
{
    return getDate(milliSeconds, FULL_DATE_FORMAT, Locale.getDefault())
}

fun getMillisFromDate(
    year: Int = 0,
    month: Int = 0,
    dayOfMonth: Int = 0,
    hour: Int = 0,
    minutes: Int = 0,
    seconds: Int = 0
): Long
{
    Log.d("Given date $year  $month  $dayOfMonth")
    val calendar: Calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth, hour, minutes, seconds)
    return calendar.timeInMillis
}

fun Long.toDateFormat(dateFormat: String = DATE_FORMAT): String
{
    return getDate(this, FULL_DATE_FORMAT)
}

fun Long.toCalendar(): Calendar
{
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return calendar
}

fun Long.toYear(): Int
{
    return toCalendar().get(Calendar.YEAR)
}

fun Long.toMonth(): Int
{
    return toCalendar().get(Calendar.MONTH)
}

fun Long.toDay(): Int
{
    return toCalendar().get(Calendar.DAY_OF_MONTH)
}

fun Fragment.setActivityTitle(id: Int)
{
    (activity as AppCompatActivity?)!!.supportActionBar?.title =
        getString(id)
}


fun Double.toStringFormatted(): String
{
    return this.toString().trimEnd('0').trimEnd(DecimalFormatSymbols.getInstance().decimalSeparator)
}

fun Double.round(places: Int, roundingMode: RoundingMode = RoundingMode.CEILING): Double
{
    return BigDecimal(this).setScale(places, roundingMode).toDouble()
}

fun SpannableStringBuilder.setSpan(what: Any): SpannableStringBuilder
{
    this.setSpan(what, 0, this.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    return this
}

