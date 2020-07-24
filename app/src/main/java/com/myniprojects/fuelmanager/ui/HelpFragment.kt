package com.myniprojects.fuelmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.myniprojects.fuelmanager.R

class HelpFragment : Fragment()
{


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment

        (activity as AppCompatActivity?)!!.supportActionBar?.title =
            getString(R.string.help_fragment_title)

        return inflater.inflate(R.layout.fragment_help, container, false)
    }

}