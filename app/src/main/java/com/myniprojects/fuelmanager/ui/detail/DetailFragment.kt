package com.myniprojects.fuelmanager.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.AppDatabase
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.databinding.FragmentDetailBinding
import com.myniprojects.fuelmanager.utils.Log
import com.myniprojects.fuelmanager.utils.OneToastFragment
import com.myniprojects.fuelmanager.utils.input


class DetailFragment : OneToastFragment()
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
                val result = Refueling.validateData(
                    binding.edTxtLitres.input,
                    binding.edTxtPrice.input,
                    binding.edTxtPreviousState.input,
                    binding.edTxtOdometerReading.input
                )

                if (result == R.string.succes_code)
                {
                    showEditConfirmation()
                }
                else
                {
                    showToast(result)
                }
            }
            else
            {
                viewModel.changeState()
            }
        }

        binding.butCancel.setOnClickListener {
            viewModel.changeState()
            with(binding)
            {
                edTxtLitres.setText(viewModel.refueling.value!!.litres.toString())
                edTxtPrice.setText(viewModel.refueling.value!!.price.toString())
                edTxtPreviousState.setText(viewModel.refueling.value!!.tankState.toString())
                edTxtPlace.setText(viewModel.refueling.value!!.place)
                edTxtOdometerReading.setText(viewModel.refueling.value!!.odometerReading.toString())
                edTxtComment.setText(viewModel.refueling.value!!.comment)
            }
        }


        binding.butDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun showEditConfirmation()
    {
        val builder = AlertDialog.Builder(requireContext(), R.style.DialogConfirmationTheme)

        builder.setTitle(getString(R.string.edit_confirmation))
        builder.setMessage(getString(R.string.edit_dialog_message))

        builder.setPositiveButton(
            "YES"
        ) { _, _ ->

            with(binding)
            {
                showToast(
                    viewModel.editRefueling(
                        edTxtLitres.input,
                        edTxtPrice.input,
                        edTxtPreviousState.input,
                        edTxtPlace.input,
                        edTxtOdometerReading.input,
                        edTxtComment.input
                    )
                )
            }
            viewModel.changeState()

        }

        builder.setNegativeButton(
            "NO", null
        )
        val alert = builder.create()
        alert.show()
    }

    private fun showDeleteConfirmation()
    {
        val builder = AlertDialog.Builder(requireContext(), R.style.DialogConfirmationTheme)

        builder.setTitle(getString(R.string.delete_refueling))
        builder.setMessage(getString(R.string.warning_delete))

        builder.setPositiveButton(
            getString(R.string.yes)
        ) { dialog, _ ->
            viewModel.deleteRefueling()
            dialog.dismiss()
            requireActivity().onBackPressed()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.share_menu, menu)

        // check if phone can handle sharing plain text
        if (null == getShareIntent().resolveActivity(requireActivity().packageManager))
        {
            menu.findItem(R.id.share)?.isVisible = false
        }
    }


    private fun getShareIntent(): Intent
    {
        return ShareCompat.IntentBuilder.from(requireActivity())
            .setText(viewModel.shareMessage)
            .setType("text/plain").intent
    }


    private fun shareFuel()
    {
        startActivity(getShareIntent())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.share ->
            {
                shareFuel()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}