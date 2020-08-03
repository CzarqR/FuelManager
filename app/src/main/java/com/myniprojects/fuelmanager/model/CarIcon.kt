package com.myniprojects.fuelmanager.model

import com.myniprojects.fuelmanager.R

data class CarIcon(val icon: Int)
{
    companion object
    {
        val cars: Array<CarIcon> = arrayOf(
            CarIcon(R.drawable.car0),
            CarIcon(R.drawable.car1),
            CarIcon(R.drawable.car2),
            CarIcon(R.drawable.car3),
            CarIcon(R.drawable.car4),
            CarIcon(R.drawable.car5),
            CarIcon(R.drawable.car6),
            CarIcon(R.drawable.bus),
            CarIcon(R.drawable.bus1),
            CarIcon(R.drawable.motorcycle),
            CarIcon(R.drawable.scooter),
            CarIcon(R.drawable.scooter1),
            CarIcon(R.drawable.taxi),
            CarIcon(R.drawable.truck),
            CarIcon(R.drawable.truck1),
            CarIcon(R.drawable.truck2),
            CarIcon(R.drawable.van)
        )
    }
}