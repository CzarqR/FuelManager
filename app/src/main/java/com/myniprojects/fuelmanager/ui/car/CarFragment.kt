package com.myniprojects.fuelmanager.ui.car

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.AppDatabase
import com.myniprojects.fuelmanager.databinding.FragmentMenuBinding
import com.myniprojects.fuelmanager.utils.Log
import kotlinx.android.synthetic.main.new_car_dialog.view.*

class CarFragment : Fragment()
{

    private lateinit var viewModel: CarFragmentVM
    private lateinit var binding: FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        Log.d("onCreateView Menu")
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_menu, container, false
        )

        // Init view model
        val application = requireNotNull(this.activity).application
        val dataSource = AppDatabase.getInstance(application).carDAO
        val viewModelFactory = CarFragmentVMFactory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CarFragmentVM::class.java)
        binding.menuViewModel = viewModel

        binding.lifecycleOwner = this

        // add car dialog
        binding.butT2.setOnClickListener {

            val mDialogView = LayoutInflater.from(context).inflate(R.layout.new_car_dialog, null)
            val mBuilder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)
            val mAlertDialog = mBuilder.show()

            val adapter = CarSpinnerAdapter(
                requireContext()
            )
            mDialogView.spinCar.adapter = adapter

            mDialogView.butAddCar.setOnClickListener {
                viewModel.addCar(
                    mDialogView.edTxtBrand.text.toString(),
                    mDialogView.edTxtModel.text.toString(),
                    mDialogView.edTxtEngine.text.toString(),
                    mDialogView.edTxtFuelType.text.toString(),
                    mDialogView.spinCar.selectedItemPosition.toByte()
                )
                mAlertDialog.dismiss()
            }

            mDialogView.butCancel.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        // listener for clicked car
        val adapter =
            CarRecyclerAdapter(CarListener {
                viewModel.carClicked(it)
            })

        viewModel.navigateToCar.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(CarFragmentDirections.actionMenuFragmentToCarFragment(it))
                viewModel.carNavigated()
            }
        })

        // RecyclerView setup
        binding.recViewCar.adapter = adapter

        viewModel.cars.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }

}