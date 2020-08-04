package com.myniprojects.fuelmanager.utils

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

open class OneToastFragment : Fragment()
{
    private lateinit var toast: Toast

    fun showToast(@StringRes text: Int, length: Int = Toast.LENGTH_SHORT)
    {
        if (this::toast.isInitialized)
        {
            toast.cancel()
        }
        toast = makeToast(text, length)
        toast.show()
    }
}