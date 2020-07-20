package com.myniprojects.fuelmanager.ui.chart


import android.os.Bundle
import android.view.*
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

        binding.chartEf.setProgressBar(binding.progressBar)
        binding.chartCost.setProgressBar(binding.progressBar)

        if (viewModel.canShow())
        {
            showChartEf()
            binding.chartEf.setChart(viewModel.chartFuelEfficiency)
        }
        else
        {
            showInfo()
            binding.progressBar.visibility = View.GONE
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.chart_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.fuelCost ->
            {
                if (viewModel.canShow())
                {
                    showChartCost()
                    binding.chartCost.setChart(viewModel.chartFuelCost)
                }
                else
                {
                    showInfo()
                }
                true
            }
            R.id.fuelEfficiency ->
            {
                if (viewModel.canShow())
                {
                    showChartEf()
                    binding.chartEf.setChart(viewModel.chartFuelEfficiency)
                }
                else
                {
                    showInfo()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showChartCost()
    {
        binding.chartEf.visibility = View.GONE
        binding.chartCost.visibility = View.VISIBLE
        binding.txtInfo.visibility = View.GONE
    }

    private fun showChartEf()
    {
        binding.chartEf.visibility = View.VISIBLE
        binding.chartCost.visibility = View.GONE
        binding.txtInfo.visibility = View.GONE
    }

    private fun showInfo()
    {
        binding.chartEf.visibility = View.GONE
        binding.chartCost.visibility = View.GONE
        binding.txtInfo.visibility = View.VISIBLE
    }
}
