package com.myniprojects.fuelmanager.ui.refueling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.AppDatabase
import com.myniprojects.fuelmanager.databinding.FragmentRefuelingBinding
import com.myniprojects.fuelmanager.utils.Log


class RefuelingFragment : Fragment()
{

    private lateinit var viewModel: RefuelingFragmentVM
    private lateinit var binding: FragmentRefuelingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        Log.d("onCreateView Car")

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_refueling, container, false
        )

        //init viewModel
        val arguments = RefuelingFragmentArgs.fromBundle(requireArguments())
        val application = requireNotNull(this.activity).application
        val dataSource = AppDatabase.getInstance(application).refuelingDAO
        val viewModelFactory = RefuelingFragmentVMFactory(dataSource, arguments.carID, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RefuelingFragmentVM::class.java)
        binding.carViewModel = viewModel

        binding.lifecycleOwner = this

        binding.buttonTest.setOnClickListener {
            viewModel.listAll()
        }

        binding.buttt2.setOnClickListener {
            viewModel.addRefueling()
        }


        val adapter =
            RefuelingRecyclerAdapter(RefuelingListener {
                viewModel.refuelingClicked(it)
            })

        binding.recViewRefueling.adapter = adapter

        viewModel.refueling.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }


}