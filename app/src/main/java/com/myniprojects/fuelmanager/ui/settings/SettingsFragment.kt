package com.myniprojects.fuelmanager.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.databinding.FragmentSettingsBinding
import com.myniprojects.fuelmanager.ui.main.MainActivityVM
import com.myniprojects.fuelmanager.utils.Log
import com.myniprojects.fuelmanager.utils.input

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

        binding.butDefault.setOnClickListener {
            showConfirmation()
        }

        setCheckingForChanges()




        return binding.root
    }

    private fun setCheckingForChanges()
    {
        binding.edTxtCurrency.addTextChangedListener {
            checkChanges()
        }

        binding.edTxtLength.addTextChangedListener {
            checkChanges()
        }

        binding.edTxtVolume.addTextChangedListener {
            checkChanges()
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, _ -> checkChanges() }
    }

    private fun checkChanges()
    {
        changeButtonState(viewModel.checkChanges(getSettings()))
    }

    private fun getSettings(): Settings
    {
        with(binding)
        {
            return Settings(
                edTxtCurrency.input,
                edTxtVolume.input,
                edTxtLength.input,
                switchDarkMode.isChecked
            )
        }
    }

    private fun changeButtonState(state: Boolean)
    {
        if (state) //clickable button
        {
            Log.d("Clickable")
            with(binding.butSave)
            {
                backgroundTintList =
                    ContextCompat.getColorStateList(requireActivity(), R.color.save_enable)
                isEnabled = true
            }

        }
        else
        {
            Log.d("Cannot save")
            with(binding.butSave)
            {
                backgroundTintList =
                    ContextCompat.getColorStateList(requireActivity(), R.color.save_disable)
                isEnabled = false
            }
        }
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


    private fun showConfirmation()
    {
        val builder = AlertDialog.Builder(requireContext(), R.style.DialogConfirmationTheme)

        builder.setTitle(getString(R.string.restore_default_settings))
        builder.setMessage(getString(R.string.warning_default_settings))

        builder.setPositiveButton(
            getString(R.string.yes)
        ) { dialog, _ ->
            viewModel.defaultSettings()
            dialog.dismiss()
        }

        builder.setNegativeButton(
            getString(R.string.no)
        ) { dialog, _ -> // Do nothing
            dialog.dismiss()
        }

        val nightModeFlags = requireContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        when (nightModeFlags)
        {
            Configuration.UI_MODE_NIGHT_YES -> builder.setIcon(R.drawable.default_setting_white)
            else -> builder.setIcon(R.drawable.default_settings)
        }


        val alert = builder.create()
        alert.show()
    }

}