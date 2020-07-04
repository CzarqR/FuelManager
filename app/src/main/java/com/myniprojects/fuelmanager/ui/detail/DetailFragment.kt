package com.myniprojects.fuelmanager.ui.detail

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
import com.myniprojects.fuelmanager.databinding.FragmentDetailBinding
import com.myniprojects.fuelmanager.utils.Log

class DetailFragment : Fragment()
{
    private lateinit var viewModel: DetailFragmentVM
    private lateinit var binding: FragmentDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        Log.d("onCreateView Detail")

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail, container, false
        )

        //init viewModel
        val arguments = DetailFragmentArgs.fromBundle(requireArguments())
        val application = requireNotNull(this.activity).application
        val dataSource = AppDatabase.getInstance(application).refuelingDAO
        val viewModelFactory =
            DetailFragmentVMFactory(dataSource, arguments.refuelingID, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DetailFragmentVM::class.java)
        binding.detailViewModel = viewModel

        binding.lifecycleOwner = this

        viewModel.editState.observe(viewLifecycleOwner, Observer {
            binding.butEdit.text = if (it)
            {
                getString(R.string.save)
            }
            else
            {
                getString(R.string.edit)
            }
        })


        return binding.root
    }

}