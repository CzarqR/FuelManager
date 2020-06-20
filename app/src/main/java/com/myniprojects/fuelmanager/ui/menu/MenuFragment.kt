package com.myniprojects.fuelmanager.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.myniprojects.fuelmanager.R
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

        viewModel = ViewModelProvider(this).get(MenuFragmentVM::class.java)

        // test
        binding.butCar.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.carFragment)
        )

        return binding.root
    }


}