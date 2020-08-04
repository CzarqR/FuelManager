package com.myniprojects.fuelmanager.ui.refueling

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.databinding.FragmentCarInfoBinding
import com.myniprojects.fuelmanager.ui.chart.ChartType


class CarInfoFragment(
    private val cars: LiveData<List<Car>>,
    private val refueling: LiveData<List<Refueling>>,
    private val goToChart: (ChartType) -> Unit
) : Fragment()
{

    lateinit var binding: FragmentCarInfoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_car_info, container, false
        )

        binding.lifecycleOwner = this

        binding.butChart.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                binding.butChart.showContextMenu(0f, 0f)
            }
            else
            {
                binding.butChart.showContextMenu()
            }
        }

        cars.observe(viewLifecycleOwner, Observer {
            binding.car = it[0]
        })

        refueling.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty())
            {
                binding.txtOdometerReading.text = getString(
                    R.string.odometer_reading_km,
                    it[0].odometerReading.toString()
                )
            }
        })

        registerForContextMenu(binding.butChart)

        return binding.root
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    )
    {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.select_chart, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.opt_chart_cost ->
            {
                goToChart(ChartType.FUEL_COST)
                true
            }
            R.id.opt_chart_ef ->
            {
                goToChart(ChartType.FUEL_EFFICIENCY)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

}