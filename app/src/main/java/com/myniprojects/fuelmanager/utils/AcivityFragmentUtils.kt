package com.myniprojects.fuelmanager.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel

fun Fragment.hideKeyboard()
{
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View)
{
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.hideKeyboard()
{
    if (this.window != null)
    {
        this.window.decorView
        val imm =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.window.decorView.windowToken, 0)

        //remove focus from EditText
        findViewById<View>(android.R.id.content).clearFocus()
    }
}

fun AndroidViewModel.getString(@StringRes id: Int): String
{
    return getApplication<Application>().getString(id)
}