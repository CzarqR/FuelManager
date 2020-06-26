package com.myniprojects.fuelmanager.ui.refueling

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.databinding.RefuelingRecyclerBinding


class RefuelingRecyclerAdapter(private val clickListener: RefuelingListener) :
        androidx.recyclerview.widget.ListAdapter<Refueling, RefuelingRecyclerAdapter.ViewHolder>(
            RefuelingDiffCallback()
        )
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.bind(getItem(position)!!, clickListener)
    }

    class ViewHolder private constructor(private val binding: RefuelingRecyclerBinding) :
            RecyclerView.ViewHolder(binding.root)
    {
        companion object
        {
            fun from(parent: ViewGroup): ViewHolder
            {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RefuelingRecyclerBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(
                    binding
                )
            }
        }


        fun bind(
            refueling: Refueling,
            clickListener: RefuelingListener
        )
        {
            binding.refueling = refueling
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }


}

class RefuelingDiffCallback : DiffUtil.ItemCallback<Refueling>()
{
    override fun areItemsTheSame(oldItem: Refueling, newItem: Refueling): Boolean
    {
        return oldItem.refuelingID == newItem.refuelingID
    }

    override fun areContentsTheSame(oldItem: Refueling, newItem: Refueling): Boolean
    {
        return oldItem == newItem
    }


}

class RefuelingListener(val clickListener: (refuelingID: Long) -> Unit)
{
    fun onClick(refueling: Refueling) = clickListener(refueling.refuelingID)
}