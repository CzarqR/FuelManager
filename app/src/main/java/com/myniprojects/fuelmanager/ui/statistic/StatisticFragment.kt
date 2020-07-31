package com.myniprojects.fuelmanager.ui.statistic

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.AppDatabase
import com.myniprojects.fuelmanager.databinding.FragmentStatisticBinding
import com.myniprojects.fuelmanager.utils.Log


class StatisticFragment : Fragment()
{
    private lateinit var viewModel: StatisticFragmentVM
    private lateinit var binding: FragmentStatisticBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        Log.d("Create fragment")

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_statistic, container, false
        )
        binding.lifecycleOwner = this

        //init viewModel
        val application = requireNotNull(this.activity).application
        val dataSourceRefueling = AppDatabase.getInstance(application).refuelingDAO
        val dataSourceCar = AppDatabase.getInstance(application).carDAO
        val arguments = StatisticFragmentArgs.fromBundle(requireArguments())

        val viewModelFactory = StatisticFragmentVMFactory(
            dataSourceRefueling,
            dataSourceCar,
            application
        )

        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(StatisticFragmentVM::class.java)

        viewModel.loadRefueling(arguments.carID)


        binding.txtStartDate.setOnClickListener {
            showDatePickerDialog()
        }

        return binding.root
    }

    private fun showDatePickerDialog()
    {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerDialogStyle,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth -> Log.d("$year $month $dayOfMonth") },
            1990, 1, 1
        )
        datePickerDialog.show()
    }


}