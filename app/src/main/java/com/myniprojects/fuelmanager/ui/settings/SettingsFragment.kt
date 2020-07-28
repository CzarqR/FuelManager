package com.myniprojects.fuelmanager.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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

        setObservers()

        binding.butSave.setOnClickListener {
            viewModel.saveSettings(
                binding.switchDarkMode.isChecked,
                binding.edTxtCurrency.text.toString(),
                binding.edTxtVolume.text.toString(),
                binding.edTxtLength.text.toString()
            )
        }




        return binding.root
    }


    private fun setObservers()
    {
        viewModel.darkTheme.observe(viewLifecycleOwner, Observer {
            binding.switchDarkMode.isChecked = it
        })

        viewModel.volumeUnit.observe(viewLifecycleOwner, Observer {
            binding.edTxtVolume.setText(it)
        })

        viewModel.lengthUnit.observe(viewLifecycleOwner, Observer {
            binding.edTxtLength.setText(it)
        })

        viewModel.currency.observe(viewLifecycleOwner, Observer {
            binding.edTxtCurrency.setText(it)
        })
    }


}