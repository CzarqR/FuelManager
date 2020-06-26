package com.myniprojects.fuelmanager.ui.car

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.AppDatabase
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
        val dataSource = AppDatabase.getInstance(application).refuelingDAO
        val viewModelFactory = CarFragmentVMFactory(dataSource, arguments.carID, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CarFragmentVM::class.java)
        binding.carViewModel = viewModel

        binding.lifecycleOwner = this

        binding.buttonTest.setOnClickListener {
            viewModel.listAll()
        }

        binding.buttt2.setOnClickListener {
            viewModel.addRefueling()
        }

        viewModel.refueling.observe(viewLifecycleOwner, Observer {
            Log.d("Block of code which is executed every time LiveData changes")
        })

        return binding.root
    }


}