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
import com.myniprojects.fuelmanager.utils.Log
import com.myniprojects.fuelmanager.utils.formatCars


class MultipleCarsFragment(private val cars: LiveData<List<Car>>) : Fragment()
{

    companion object
    {
        private const val SIZE = 3

    }

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

        Log.d("Setting height in onCreate method")
        binding.scrollRoot.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            binding.txtCars.lineHeight * SIZE + 10
        )


        cars.observe(viewLifecycleOwner, Observer {
            if (it.size < SIZE)
            {
                Log.d("Setting height in observer method")
                val layoutParams = binding.scrollRoot.layoutParams
                layoutParams.height = binding.txtCars.lineHeight * (it.size) + 10
            }
            binding.txtCars.text = formatCars(it, requireContext())
        })

        return binding.root
    }


}