package com.myniprojects.fuelmanager.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
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
                showEditConfirmation()
            }
            viewModel.changeState()
        }

        binding.butCancel.setOnClickListener {
            viewModel.changeState()
            with(binding)
            {
                edTxtLitres.setText(viewModel.refueling.value!!.litres.toString())
                edTxtPrice.setText(viewModel.refueling.value!!.price.toString())
                edTxtPreviousState.setText(viewModel.refueling.value!!.previousTankState.toString())
                edTxtPlace.setText(viewModel.refueling.value!!.place)
                edTxtOdometerReading.setText(viewModel.refueling.value!!.previousOdometerReading.toString())
                edTxtComment.setText(viewModel.refueling.value!!.comment)
            }
        }


        binding.butDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        setHasOptionsMenu(true)

        (activity as AppCompatActivity?)!!.supportActionBar?.title =
            getString(R.string.detail_fragment_title)

        return binding.root
    }

    private fun showEditConfirmation()
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
                binding.edTxtOdometerReading.text.toString().toDouble(),
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

    private fun showDeleteConfirmation()
    {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Confirm")
        builder.setMessage("Are you sure?")

        builder.setPositiveButton(
            "YES"
        ) { dialog, _ ->
            viewModel.deleteRefueling()
            dialog.dismiss()
            requireActivity().onBackPressed()
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
            .setText(getString(R.string.share_message, "Place", "11.98"))
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