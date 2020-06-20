package com.myniprojects.fuelmanager.utils

import android.util.Log

class Log
{
    companion object
    {
        private const val tag: String = "AppDebug"

        fun d(text: Any, tag: Any = this.tag)
        {
            Log.d(tag.toString(), text.toString())
        }
    }
}