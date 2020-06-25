package com.myniprojects.fuelmanager.ui.car

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.CarDatabase
import com.myniprojects.fuelmanager.databinding.FragmentCarBinding
import com.myniprojects.fuelmanager.utils.Log


class CarFragment : Fragment() {

    private lateinit var viewModel: CarFragmentVM
    private lateinit var binding: FragmentCarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        Log.d("onCreateView Car")

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_car, container, false
        )

        //init viewModel
        val arguments = CarFragmentArgs.fromBundle(requireArguments())
        val application = requireNotNull(this.activity).application
        val dataSource = CarDatabase.getInstance(application).carDAO
        val viewModelFactory = CarFragmentVMFactory(dataSource, arguments.carID, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CarFragmentVM::class.java)
        binding.carViewModel = viewModel

        binding.lifecycleOwner = this

        return binding.root
    }


}