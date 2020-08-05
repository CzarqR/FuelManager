package com.myniprojects.fuelmanager.ui.car

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.AppDatabase
import com.myniprojects.fuelmanager.databinding.FragmentCarBinding
import com.myniprojects.fuelmanager.utils.*
import kotlinx.android.synthetic.main.new_car_dialog.view.*


class CarFragment : OneToastFragment()
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

        // options bar
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Created Car")
        setup()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        super.onCreateOptionsMenu(menu, inflater)
        MenuCompat.setGroupDividerEnabled(menu, true)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {

        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        )


        val builder = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.enter_left_to_right)
            .setExitAnim(R.anim.exit_right_to_left)
            .setPopEnterAnim(R.anim.popenter_right_to_left)
            .setPopExitAnim(R.anim.popexit_left_to_right)


        val options = builder.build()
        return try
        {
            requireView().findNavController().navigate(item.itemId, null, options)
            true
        } catch (e: IllegalArgumentException)
        {
            false
        }

    }

    private fun setup()
    {
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


            with(mDialogView)
            {
                spinCar.adapter = adapter
                txtTitle.text = getString(R.string.add_new_car)
                butAddCar.text = getString(R.string.add_car)

                butAddCar.setOnClickListener {

                    val exitCode = viewModel.addCar(

                        edTxtBrand.input,
                        edTxtModel.input,
                        edTxtEngine.input,
                        edTxtFuelType.input,
                        edTxtTankSize.input,
                        spinCar.selectedItemPosition.toByte()
                    )

                    if (exitCode == R.string.car_added)
                    {
                        mAlertDialog.dismiss()
                    }

                    showToast(exitCode)

                }

                butCancel.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }


        }

        // listener for clicked car


        val carListener = CarListener(
            { carID -> viewModel.carClicked(longArrayOf(carID)) }, // click
            { carID -> editCarDialog(carID) }, // long click
            { carID -> showConfirmation(carID) }, // delete
            { dy -> binding.recViewCar.scrollBy(0, dy) }, // scroll
            {
                showToast(R.string.cannot_select_car)
            }

        )


        val adapter =
            CarRecyclerAdapter(carListener, resources.getIntArray(R.array.car_colors).size)

        binding.butSelectedMany.setOnClickListener {
            val longArray: LongArray = adapter.selectedCars.value!!.toLongArray()
            viewModel.carClicked(longArray)
        }

        adapter.selectedCars.observe(viewLifecycleOwner, Observer {
            if (it.size > 0)
            {
                binding.butSelectedMany.visibility = View.VISIBLE
                binding.butStatistic.visibility = View.VISIBLE
            }
            else
            {
                binding.butSelectedMany.visibility = View.INVISIBLE
                binding.butStatistic.visibility = View.INVISIBLE
            }
        })

        viewModel.navigateToRefueling.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(CarFragmentDirections.actionCarToRefueling(it))
                viewModel.carNavigated()
            }
        })

        binding.butStatistic.setOnClickListener {
            this.findNavController()
                .navigate(CarFragmentDirections.actionCarToStatistic(adapter.selectedCars.value?.toLongArray()))
        }

        // RecyclerView setup
        binding.recViewCar.adapter = adapter
        binding.recViewCar.addItemDecoration(TopSpacingItemDecoration(10))

        viewModel.cars.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                if (it.isEmpty())
                {
                    setActivityTitle(R.string.add_car)
                }
                else
                {
                    setActivityTitle(R.string.car_fragment_title)
                }
            }
        })
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


        with(mDialogView)
        {


            spinCar.adapter = adapter
            txtTitle.text = getString(R.string.edit_car)
            butAddCar.text = getString(R.string.update)

            val car = viewModel.getCar(carID)

            edTxtBrand.setText(car.brand)
            edTxtModel.setText(car.model)
            edTxtEngine.setText(car.engine)
            edTxtFuelType.setText(car.fuelType)
            edTxtTankSize.setText(car.tankSize.toString())

            spinCar.setSelection(car.iconID.toInt())

            butAddCar.setOnClickListener {


                val exitCode = viewModel.editCar(
                    edTxtBrand.text.toString(),
                    edTxtModel.text.toString(),
                    edTxtEngine.text.toString(),
                    edTxtFuelType.text.toString(),
                    spinCar.selectedItemPosition.toByte(),
                    edTxtTankSize.text.toString(),
                    carID
                )

                if (exitCode == R.string.car_edited)
                {
                    mAlertDialog.dismiss()
                }

                showToast(exitCode)
            }

            mDialogView.butCancel.setOnClickListener {
                mAlertDialog.dismiss()
            }

        }
    }

    private fun showConfirmation(carID: Long)
    {
        val builder = AlertDialog.Builder(requireContext(), R.style.DialogConfirmationTheme)

        builder.setTitle(getString(R.string.delete_car))
        builder.setMessage(getString(R.string.warning_delete))

        builder.setPositiveButton(
            getString(R.string.yes)
        ) { dialog, _ ->
            viewModel.deleteCar(carID)
            dialog.dismiss()
        }

        builder.setNegativeButton(
            getString(R.string.no)
        ) { dialog, _ -> // Do nothing
            dialog.dismiss()
        }

        builder.setIcon(R.drawable.delete)

        val alert = builder.create()
        alert.show()
    }

}
