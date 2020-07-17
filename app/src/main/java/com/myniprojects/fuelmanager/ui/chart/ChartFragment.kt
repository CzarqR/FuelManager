package com.myniprojects.fuelmanager.ui.chart


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.databinding.FragmentChartBinding
import com.myniprojects.fuelmanager.ui.refueling.RefuelingFragmentVM
import com.myniprojects.fuelmanager.utils.Log

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


        viewModel.refueling.observe(viewLifecycleOwner, Observer {
            it.forEach {
                Log.d(it)
            }
        })

        //error with VM

        //chart test

        binding.chart.setProgressBar(binding.progressBar)


        binding.chart.setChart(viewModel.chart)

        return binding.root
    }



}