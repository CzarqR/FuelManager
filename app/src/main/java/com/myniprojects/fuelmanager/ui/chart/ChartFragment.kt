package com.myniprojects.fuelmanager.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
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


        viewModel.cars.observe(viewLifecycleOwner, Observer {
            it.forEach {
                Log.d(it)
            }
        })

        //error with VM

        //chart test
        val pie = AnyChart.pie()
        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("John", 10000))
        data.add(ValueDataEntry("Jake", 12000))
        data.add(ValueDataEntry("Peter", 18000))
        pie.data(data)

        binding.chart.setChart(pie)




        return binding.root
    }

}