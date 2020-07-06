package com.myniprojects.fuelmanager.ui.refueling

import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myniprojects.fuelmanager.R
import kotlinx.android.synthetic.main.fragment_multiple_cars.*


class MultipleCarsFragment(private val cars: Spanned) : Fragment()
{

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_multiple_cars, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        txtCars.text = cars
    }


}