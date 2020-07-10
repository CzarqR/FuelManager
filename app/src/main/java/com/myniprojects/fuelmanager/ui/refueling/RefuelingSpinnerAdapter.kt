package com.myniprojects.fuelmanager.ui.refueling

import android.content.Context
import android.text.Spanned
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.myniprojects.fuelmanager.R
import kotlinx.android.synthetic.main.refuling_spinner.view.*


class RefuelingSpinnerAdapter(context: Context, private val carsNames: List<Spanned>) :
        BaseAdapter()
{
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItem(p0: Int): Any
    {
        return carsNames[p0]
    }

    override fun getItemId(p0: Int): Long
    {
        return p0.toLong()
    }

    override fun getCount(): Int
    {
        return carsNames.size
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View
    {
        return if (p1 == null)
        {
            val view = inflater.inflate(R.layout.refuling_spinner, p2, false)
            view.txtCarName.text = carsNames[p0]
            view
        }
        else
        {
            p1.txtCarName.text = carsNames[p0]
            p1
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        val v = super.getDropDownView(position, convertView, parent)
        (v as TextView).gravity = Gravity.END
        return v
    }

}