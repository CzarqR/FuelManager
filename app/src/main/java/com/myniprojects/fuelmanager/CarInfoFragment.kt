package com.myniprojects.fuelmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.databinding.FragmentCarInfoBinding

class CarInfoFragment(private val car: Car) : Fragment()
{

    private lateinit var binding: FragmentCarInfoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_car_info, container, false
        )

        binding.car = car

        return binding.root
    }


}