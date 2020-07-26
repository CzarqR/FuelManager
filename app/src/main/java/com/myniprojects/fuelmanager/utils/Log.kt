package com.myniprojects.fuelmanager.utils

import android.util.Log

class Log
{
    companion object
    {
        private const val tag: String = "AppDebug"

        fun d(text: Any?, tag: Any = this.tag)
        {
            if (text == null)
            {
                Log.d(tag.toString(), "Argument was NULL")
            }
            else
            {
                Log.d(tag.toString(), text.toString())
            }
        }
    }
}