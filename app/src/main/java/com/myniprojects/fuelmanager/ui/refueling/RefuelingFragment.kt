package com.myniprojects.fuelmanager.ui.refueling

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
import androidx.recyclerview.widget.DividerItemDecoration
import com.myniprojects.fuelmanager.CarInfoFragment
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.AppDatabase
import com.myniprojects.fuelmanager.databinding.FragmentRefuelingBinding
import com.myniprojects.fuelmanager.utils.Log
import kotlinx.android.synthetic.main.new_refueling_dialog.view.*


class RefuelingFragment : Fragment()
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
        viewModel = ViewModelProvider(this, viewModelFactory).get(RefuelingFragmentVM::class.java)
        binding.carViewModel = viewModel

        binding.lifecycleOwner = this


        // add refueling dialog
        binding.butAddRefueling.setOnClickListener {

            val mDialogView =
                LayoutInflater.from(context).inflate(R.layout.new_refueling_dialog, null)
            val mBuilder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)
            val mAlertDialog = mBuilder.show()

            mDialogView.butAddRefueling.setOnClickListener {
                with(mDialogView)
                {
                    viewModel.addRefueling(
                        edTxtLitres.text.toString().toDouble(),
                        edTxtPrice.text.toString().toDouble(),
                        edTxtPreviousState.text.toString().toByte(),
                        edTxtPlace.text.toString(),
                        edTxtComment.text.toString()
                    )
                    mAlertDialog.dismiss()
                }
            }

            mDialogView.butCancelRef.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        // set RecyclerView
        val adapter =
            RefuelingRecyclerAdapter(RefuelingListener {
                viewModel.refuelingClicked(it)
            })

        viewModel.navigateToRefueling.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(RefuelingFragmentDirections.actionRefuelingToDetail(it))
                viewModel.refuelingNavigated()
            }
        })

        binding.recViewRefueling.adapter = adapter
        binding.recViewRefueling.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        viewModel.refueling.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        val carInfoFragment = CarInfoFragment(viewModel.car.value!!)

        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentCarInfo, carInfoFragment).commit()
    }

}