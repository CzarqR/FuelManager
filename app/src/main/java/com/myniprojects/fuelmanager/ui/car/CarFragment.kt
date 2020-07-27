package com.myniprojects.fuelmanager.ui.car

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.AppDatabase
import com.myniprojects.fuelmanager.databinding.FragmentCarBinding
import com.myniprojects.fuelmanager.ui.main.MainActivity
import com.myniprojects.fuelmanager.utils.Log
import com.myniprojects.fuelmanager.utils.TopSpacingItemDecoration
import com.myniprojects.fuelmanager.utils.setActivityTitle
import kotlinx.android.synthetic.main.new_car_dialog.view.*


class CarFragment : Fragment()
{

    companion object
    {
        const val THEME_KEY: String = "THEME_KEY"
    }

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
                    mDialogView.edTxtTankSize.text.toString().toDouble(),
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

        viewModel.navigateToRefueling.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(CarFragmentDirections.actionCarToRefueling(it))
                viewModel.carNavigated()
            }
        })

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


        // options bar
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        super.onCreateOptionsMenu(menu, inflater)
        MenuCompat.setGroupDividerEnabled(menu, true)
        inflater.inflate(R.menu.overflow_menu, menu)

        //set checked in dark mode item
        val item = menu.findItem(R.id.darkMode)
        item.isChecked = MainActivity.darkThemeStyle
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.aboutFragment -> NavigationUI.onNavDestinationSelected(
                item,
                requireView().findNavController()
            )
            R.id.darkMode ->
            {
                item.isChecked = !item.isChecked
                val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                if (!item.isChecked) //change to day colors
                {
                    Log.d("Change to day")
                    with(sharedPref.edit()) {
                        putBoolean(THEME_KEY, false)
                        apply()
                    }
                }
                else //change to dark mode
                {
                    Log.d("Change to dark")
                    with(sharedPref.edit()) {
                        putBoolean(THEME_KEY, true)
                        apply()
                    }
                }

                requireActivity().recreate()

                true
            }
            R.id.ourApps ->
            {
                Log.d("Our apps")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
        mDialogView.edTxtTankSize.setText(car.tankSize.toString())

        mDialogView.spinCar.setSelection(car.iconID.toInt())

        mDialogView.butAddCar.setOnClickListener {
            viewModel.editCar(
                mDialogView.edTxtBrand.text.toString(),
                mDialogView.edTxtModel.text.toString(),
                mDialogView.edTxtEngine.text.toString(),
                mDialogView.edTxtFuelType.text.toString(),
                mDialogView.spinCar.selectedItemPosition.toByte(),
                mDialogView.edTxtTankSize.text.toString().toDouble(),
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
