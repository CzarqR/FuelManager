package com.myniprojects.fuelmanager.model

import com.myniprojects.fuelmanager.R

data class CarIcon(val icon: Int, val name: Int)
{
    companion object
    {
        val cars: Array<CarIcon> = arrayOf(
            CarIcon(R.drawable.car0, R.string.car0),
            CarIcon(R.drawable.car1, R.string.car1),
            CarIcon(R.drawable.car2, R.string.car2),
            CarIcon(R.drawable.car3, R.string.car3),
            CarIcon(R.drawable.car4, R.string.car4),
            CarIcon(R.drawable.car5, R.string.car5),
            CarIcon(R.drawable.car6, R.string.car6)
        )


    }
}


//        fun carsNames(resources: Resources): Array<String>
//        {
//            return Array(cars.size) { i -> resources.getString(cars[i].name) }
//        }
//
//        fun getResource(index: Int): Int
//        {
//            return cars[index].icon
//        }
//
//        fun getString(index: Int): Int
//        {
//            return cars[index].name
//        }