package com.myniprojects.fuelmanager.ui.refueling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.AppDatabase
import com.myniprojects.fuelmanager.databinding.FragmentRefuelingBinding
import com.myniprojects.fuelmanager.utils.*
import kotlinx.android.synthetic.main.new_refueling_dialog.view.*


class RefuelingFragment : OneToastFragment()
{

    private lateinit var viewModel: RefuelingFragmentVM
    private lateinit var binding: FragmentRefuelingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        Log.d("onCreateView Refueling")

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_refueling, container, false
        )

        //init viewModel
        val arguments = RefuelingFragmentArgs.fromBundle(requireArguments())
        val application = requireNotNull(this.activity).application
        val dataSourceRefueling = AppDatabase.getInstance(application).refuelingDAO
        val dataSourceCar = AppDatabase.getInstance(application).carDAO
        val viewModelFactory = RefuelingFragmentVMFactory(
            dataSourceRefueling,
            dataSourceCar,
            arguments.carID,
            application
        )
        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(RefuelingFragmentVM::class.java)

        viewModel.setCarId(arguments.carID)

        binding.carViewModel = viewModel

        binding.lifecycleOwner = this


        // add refueling dialog
        binding.butAddRefueling.setOnClickListener {

            val mDialogView =
                LayoutInflater.from(context).inflate(R.layout.new_refueling_dialog, null)
            val mBuilder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)
            val mAlertDialog = mBuilder.show()


            mDialogView.spinnerCars.adapter = CarSpinnerAdapter(
                requireContext(),
                getCarNamesFormatted(viewModel.cars.value!!, requireContext())
            )

            mDialogView.butAddRefueling.setOnClickListener {
                with(mDialogView)
                {

                    val exitCode = viewModel.addRefueling(
                        edTxtLitres.input,
                        edTxtPrice.input,
                        edTxtTankState.input,
                        edTxtPlace.input,
                        edTxtComment.input,
                        edTxtOdometerReading.input,
                        mDialogView.spinnerCars.selectedItemPosition
                    )

                    showToast(exitCode)

                    if (exitCode == R.string.refueling_added)
                    {
                        mAlertDialog.dismiss()
                    }
                }
            }

            mDialogView.butCancelRef.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }


        // set RecyclerView
        val adapter =
            RefuelingRecyclerAdapter(
                viewModel.carID,
                RefuelingListener {
                    viewModel.refuelingClicked(it)
                }
            )

        viewModel.navigateToRefueling.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(RefuelingFragmentDirections.actionRefuelingToDetail(it))
                viewModel.refuelingNavigated()
            }
        })

        binding.recViewRefueling.adapter = adapter
        binding.recViewRefueling.addItemDecoration(TopSpacingItemDecoration(10))

        viewModel.refueling.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                binding.recViewRefueling.smoothScrollToPosition(0)
            }
        })



        viewModel.cars.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.size > 1)
                {
                    setActivityTitle(R.string.refueling_many_fragment_title)
                }
            }
        })

        viewModel.navigateToChart.observe(viewLifecycleOwner, Observer {

            if (it != null)
            {
                if (viewModel.canShow())
                {
                    this.findNavController()
                        .navigate(RefuelingFragmentDirections.actionRefuelingToChart())
                    viewModel.chartNavigated()
                }
                else
                {
                    showToast(R.string.info_charts_cannot_display)
                }
            }
        })

        return binding.root
    }

    override fun onStop()
    {
        super.onStop()
        viewModel.chartNavigated() //to clear toast
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Create")

        if (viewModel.type) // 1 car
        {

            val carInfoFragment = CarInfoFragment()
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentCarInfo, carInfoFragment).commit()
        }
        else // many cars
        {
            val multipleCarsFragment =
                MultipleCarsFragment()
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentCarInfo, multipleCarsFragment).commit()
        }
    }
}