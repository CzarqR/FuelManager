package com.myniprojects.fuelmanager.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.CarDatabase
import com.myniprojects.fuelmanager.databinding.FragmentMenuBinding
import com.myniprojects.fuelmanager.utils.Log

class MenuFragment : Fragment()
{

    private lateinit var viewModel: MenuFragmentVM
    private lateinit var binding: FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        Log.d("onCreateView Menu")
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_menu, container, false
        )

        // Init view model
        val application = requireNotNull(this.activity).application
        val dataSource = CarDatabase.getInstance(application).carDAO
        val viewModelFactory = MenuFragmentVMFactory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MenuFragmentVM::class.java)
        binding.menuViewModel = viewModel

        binding.lifecycleOwner = this

        // navigate
//        binding.butT1.setOnClickListener(
//            Navigation.createNavigateOnClickListener(R.id.carFragment)
//        )

        binding.butT2.setOnClickListener {
            viewModel.addCar()
        }

        return binding.root
    }


}