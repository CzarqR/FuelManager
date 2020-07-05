package com.myniprojects.fuelmanager.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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
            binding.butEdit.text = if (it) //save
            {
                binding.butCancel.visibility = View.VISIBLE
                getString(R.string.save)
            }
            else //start editing
            {
                binding.butCancel.visibility = View.GONE
                getString(R.string.edit)
            }
        })

        binding.butEdit.setOnClickListener {
            if (viewModel.editState.value!!)
            {
                showConfirmation()
            }
            viewModel.changeState()
        }

        binding.butCancel.setOnClickListener {
            viewModel.changeState()
            binding.edTxtLitres.setText(viewModel.refueling.value!!.litres.toString())
            binding.edTxtPrice.setText(viewModel.refueling.value!!.price.toString())
            binding.edTxtPreviousState.setText(viewModel.refueling.value!!.previousState.toString())
            binding.edTxtPlace.setText(viewModel.refueling.value!!.place)
            binding.edTxtComment.setText(viewModel.refueling.value!!.comment)
        }


        return binding.root
    }


    private fun showConfirmation()
    {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Confirm")
        builder.setMessage("Are you sure?")

        builder.setPositiveButton(
            "YES"
        ) { dialog, _ ->
            viewModel.editRefueling(
                binding.edTxtLitres.text.toString().toDouble(),
                binding.edTxtPrice.text.toString().toDouble(),
                binding.edTxtPreviousState.text.toString().toByte(),
                binding.edTxtPlace.text.toString(),
                binding.edTxtComment.text.toString()
            )
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