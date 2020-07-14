package com.myniprojects.fuelmanager.ui.refueling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.databinding.FragmentCarInfoBinding

class CarInfoFragment(private val cars: LiveData<List<Car>>) : Fragment()
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

        binding.lifecycleOwner = this


        cars.observe(viewLifecycleOwner, Observer {
            binding.car = it[0]
        })


        return binding.root
    }


}