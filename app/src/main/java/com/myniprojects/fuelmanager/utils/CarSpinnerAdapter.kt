package com.myniprojects.fuelmanager.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.model.CarIcon
import kotlinx.android.synthetic.main.spinner_car.view.*


class CarSpinnerAdapter(context: Context) : BaseAdapter()
{
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItem(p0: Int): Any
    {
        return CarIcon.cars[p0]
    }

    override fun getItemId(p0: Int): Long
    {
        return p0.toLong()
    }

    override fun getCount(): Int
    {
        return CarIcon.cars.size
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View
    {
        return if (p1 == null)
        {
            val view = inflater.inflate(R.layout.spinner_car, p2, false)
            view.imgCar.setImageResource(CarIcon.cars[p0].icon)
            view
        }
        else
        {
            p1.imgCar.setImageResource(CarIcon.cars[p0].icon)
            p1
        }
    }
}


class CarListener(val clickListener: (carId: Long) -> Unit)
{
    fun onClick(car: Car) = clickListener(car.carID)
}