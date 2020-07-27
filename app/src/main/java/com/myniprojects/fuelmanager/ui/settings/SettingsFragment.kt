package com.myniprojects.fuelmanager.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.databinding.FragmentSettingsBinding
import com.myniprojects.fuelmanager.ui.main.MainActivityVM

class SettingsFragment : Fragment()
{
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: MainActivityVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_settings, container, false
        )
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(requireActivity()).get(MainActivityVM::class.java)


        return binding.root
    }

}