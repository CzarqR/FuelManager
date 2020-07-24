package com.myniprojects.fuelmanager.ui.chart


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.databinding.FragmentChartBinding
import com.myniprojects.fuelmanager.ui.refueling.RefuelingFragmentVM

class ChartFragment : Fragment()
{

    private val viewModel: RefuelingFragmentVM by activityViewModels()

    private lateinit var binding: FragmentChartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chart, container, false
        )

        binding.lifecycleOwner = this

        //chart test

        binding.chart.setProgressBar(binding.progressBar)

        when (viewModel.chartType)
        {
            ChartType.FUEL_EFFICIENCY ->
            {
                binding.chart.setChart(viewModel.chartFuelEfficiency)
            }
            ChartType.FUEL_COST ->
            {
                binding.chart.setChart(viewModel.chartFuelCost)
            }
//            else ->
//            {
//                throw IllegalArgumentException("Chart type couldn't be handled")
//            }
        }

        return binding.root
    }
}

enum class ChartType
{
    FUEL_EFFICIENCY, FUEL_COST
}