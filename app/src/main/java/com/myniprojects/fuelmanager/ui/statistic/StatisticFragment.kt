package com.myniprojects.fuelmanager.ui.statistic

import android.app.DatePickerDialog
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
import com.myniprojects.fuelmanager.databinding.FragmentStatisticBinding
import com.myniprojects.fuelmanager.ui.main.MainActivity
import com.myniprojects.fuelmanager.utils.*


class StatisticFragment : Fragment()
{
    private lateinit var viewModel: StatisticFragmentVM
    private lateinit var binding: FragmentStatisticBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_statistic, container, false
        )

        binding.lifecycleOwner = this

        initViewModel()

        setObservers()

        setOnClick()

        return binding.root
    }

    private fun initViewModel()
    {
        val application = requireNotNull(this.activity).application
        val dataSourceRefueling = AppDatabase.getInstance(application).refuelingDAO
        val arguments = StatisticFragmentArgs.fromBundle(requireArguments())

        val viewModelFactory = StatisticFragmentVMFactory(
            dataSourceRefueling,
            application
        )

        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(StatisticFragmentVM::class.java)

        viewModel.loadRefueling(arguments.carID)
    }

    private fun setObservers()
    {
        viewModel.startDateSelected.observe(viewLifecycleOwner, Observer {
            binding.txtStartDateSelected.text = it.toDateFormat()
        })

        viewModel.endDateSelected.observe(viewLifecycleOwner, Observer {
            binding.txtEndDateSelected.text = it.toDateFormat()
        })

        viewModel.canShowStatistic.observe(viewLifecycleOwner, Observer {
            if (it)
            {
                Log.d("Can show stats")
                binding.linLayStatistic.visibility = View.VISIBLE
                binding.frameCannotShowStats.visibility = View.GONE
            }
            else
            {
                Log.d("Cannot show stats")
                binding.linLayStatistic.visibility = View.GONE
                binding.frameCannotShowStats.visibility = View.VISIBLE
            }
        })

        viewModel.isFuelInDateRange.observe(viewLifecycleOwner, Observer {
            if (it)
            {
                Log.d("Data to show")
                binding.frameNoRefuelingInDataRange.visibility = View.GONE
                binding.scrollStatistic.visibility = View.VISIBLE
            }
            else
            {
                Log.d("No data to show")
                binding.frameNoRefuelingInDataRange.visibility = View.VISIBLE
                binding.scrollStatistic.visibility = View.GONE
            }
        })

        // region stats

        viewModel.numbersOfRefueling.observe(viewLifecycleOwner, Observer {
            Log.d("Numbers $it")
            binding.txtNumberOfRefueling.text = getString(R.string.numbers_refueling, it)
        })

        viewModel.totalPrice.observe(viewLifecycleOwner, Observer {
            Log.d("Total Price $it")
            binding.txtTotalSum.text =
                getString(
                    R.string.total_sum,
                    it.round(2).toStringFormatted(),
                    MainActivity.currency
                )
        })

        viewModel.totalLitres.observe(viewLifecycleOwner, Observer {
            Log.d("Total litres: $it")
            binding.txtTotalLitres.text =
                getString(
                    R.string.total_litres,
                    it.round(3).toStringFormatted(),
                    MainActivity.volumeUnit
                )
        })

        viewModel.mostExpensiveRefueling.observe(viewLifecycleOwner, Observer {
            Log.d("Most exp: $it")
            binding.txtMostExpensive.text = getString(
                R.string.most_expensive,
                it.round(2).toStringFormatted(),
                MainActivity.currency
            )
        })

        viewModel.cheapestRefueling.observe(viewLifecycleOwner, Observer {
            Log.d("Cheapest: $it")
            binding.txtCheapest.text = getString(
                R.string.cheapest_refueling,
                it.round(2).toStringFormatted(),
                MainActivity.currency
            )
        })

        viewModel.averagePrice.observe(viewLifecycleOwner, Observer {
            Log.d("Avg: $it")
            binding.txtAvgPrice.text = getString(
                R.string.average_price,
                MainActivity.volumeUnit,
                it.round(2).toStringFormatted(),
                MainActivity.currency
            )
        })

        // endregion

    }

    private fun setOnClick()
    {
        binding.txtStartDateSelected.setOnClickListener {
            showDatePickerDialog(
                viewModel.startDateSelected.value!!,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    viewModel.setStartDate(
                        year,
                        month,
                        dayOfMonth
                    )
                })
        }

        binding.txtEndDateSelected.setOnClickListener {
            showDatePickerDialog(
                viewModel.endDateSelected.value!!,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    viewModel.setEndDate(
                        year,
                        month,
                        dayOfMonth
                    )
                })
        }
    }

    private fun showDatePickerDialog(millis: Long, listener: DatePickerDialog.OnDateSetListener)
    {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerDialogStyle,
            listener,
            millis.toYear(),
            millis.toMonth(),
            millis.toDay()
        )
        datePickerDialog.show()
    }


}