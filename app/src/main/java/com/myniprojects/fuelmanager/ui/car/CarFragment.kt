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
import com.myniprojects.fuelmanager.databinding.FragmentCarBinding
import com.myniprojects.fuelmanager.utils.Log
import kotlinx.android.synthetic.main.new_car_dialog.view.*


class CarFragment : Fragment()
{

    private lateinit var viewModel: CarFragmentVM
    private lateinit var binding: FragmentCarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        Log.d("onCreateView Menu")
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_car, container, false
        )

        // Init view model
        val application = requireNotNull(this.activity).application
        val dataSource = AppDatabase.getInstance(application).carDAO
        val viewModelFactory = CarFragmentVMFactory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CarFragmentVM::class.java)
        binding.menuViewModel = viewModel

        binding.lifecycleOwner = this

        // add car dialog
        binding.butAddCar.setOnClickListener {
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.new_car_dialog, null)
            val mBuilder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)
            val mAlertDialog = mBuilder.show()

            val adapter = CarSpinnerAdapter(
                requireContext()
            )

            mDialogView.spinCar.adapter = adapter
            mDialogView.txtTitle.text = getString(R.string.add_new_car)
            mDialogView.butAddCar.text = getString(R.string.add_car)

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


        val carListener = CarListener(
            { carID -> viewModel.carClicked(longArrayOf(carID)) }, // click
            { carID -> editCarDialog(carID) }, // long click
            { carID -> showConfirmation(carID) }, // delete
            { dy -> binding.recViewCar.scrollBy(0, dy) } // scroll
        )

        val adapter =
            CarRecyclerAdapter(carListener)

        binding.butSelectedMany.setOnClickListener {
            val longArray: LongArray = adapter.selectedCars.value!!.toLongArray()
            viewModel.carClicked(longArray)
        }

        adapter.selectedCars.observe(viewLifecycleOwner, Observer {
            Log.d("Selected cars observed")
            if (it.size > 0)
            {
                binding.butSelectedMany.visibility = View.VISIBLE
            }
            else
            {
                binding.butSelectedMany.visibility = View.INVISIBLE
            }
        })

        viewModel.navigateToCar.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(CarFragmentDirections.actionCarToRefueling(it))
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

    private fun editCarDialog(carID: Long)
    {
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.new_car_dialog, null)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()

        val adapter = CarSpinnerAdapter(
            requireContext()
        )

        mDialogView.spinCar.adapter = adapter
        mDialogView.txtTitle.text = getString(R.string.edit_car)
        mDialogView.butAddCar.text = getString(R.string.update)

        val car = viewModel.getCar(carID)

        mDialogView.edTxtBrand.setText(car.brand)
        mDialogView.edTxtModel.setText(car.model)
        mDialogView.edTxtEngine.setText(car.engine)
        mDialogView.edTxtFuelType.setText(car.fuelType)

        mDialogView.spinCar.setSelection(car.iconID.toInt())

        mDialogView.butAddCar.setOnClickListener {
            viewModel.editCar(
                mDialogView.edTxtBrand.text.toString(),
                mDialogView.edTxtModel.text.toString(),
                mDialogView.edTxtEngine.text.toString(),
                mDialogView.edTxtFuelType.text.toString(),
                mDialogView.spinCar.selectedItemPosition.toByte(),
                carID
            )
            mAlertDialog.dismiss()
        }

        mDialogView.butCancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun showConfirmation(carID: Long)
    {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Confirm")
        builder.setMessage("Are you sure?")

        builder.setPositiveButton(
            "YES"
        ) { dialog, _ ->
            viewModel.deleteCar(carID)
            dialog.dismiss()
        }

        builder.setNegativeButton(
            "NO"
        ) { dialog, _ -> // Do nothing
            Log.d("No")
            dialog.dismiss()
        }

        val alert = builder.create()
        alert.show()
    }

}
