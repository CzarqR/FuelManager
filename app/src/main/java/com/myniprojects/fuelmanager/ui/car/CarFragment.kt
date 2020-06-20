package com.myniprojects.fuelmanager.ui.car

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.databinding.FragmentCarBinding


class CarFragment : Fragment() {

    private lateinit var binding: FragmentCarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_car, container, false)

        return binding.root
    }


}