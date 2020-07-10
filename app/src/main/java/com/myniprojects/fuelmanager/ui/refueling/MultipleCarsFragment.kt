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
import com.myniprojects.fuelmanager.databinding.FragmentMultipleCarsBinding
import com.myniprojects.fuelmanager.utils.formatCars


class MultipleCarsFragment(private val cars: LiveData<List<Car>>) : Fragment()
{

    private lateinit var binding: FragmentMultipleCarsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_multiple_cars, container, false
        )

        binding.lifecycleOwner = this

        binding.scrollRoot.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            binding.txtCars.lineHeight * 3 + 10
        )





        cars.observe(viewLifecycleOwner, Observer {
            binding.txtCars.text = formatCars(it, requireContext())
        })

        return binding.root
    }


}