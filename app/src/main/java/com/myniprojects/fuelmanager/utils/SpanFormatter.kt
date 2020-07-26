package com.myniprojects.fuelmanager.utils

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString
import java.util.*
import java.util.regex.Pattern

/**
 * @author of Java version: George T. Steel
 */
object SpanFormatter
{
    private val FORMAT_SEQUENCE: Pattern =
        Pattern.compile("%([0-9]+\\$|<?)([^a-zA-z%]*)([[a-zA-Z%]&&[^tT]]|[tT][a-zA-Z])")

    fun format(format: CharSequence?, vararg args: Any?): SpannedString
    {
        return format(Locale.getDefault(), format, *args)
    }

    fun format(
        locale: Locale?,
        format: CharSequence?,
        vararg args: Any?
    ): SpannedString
    {
        val out = SpannableStringBuilder(format)
        var i = 0
        var argAt = -1
        while (i < out.length)
        {
            val m = FORMAT_SEQUENCE.matcher(out)
            if (!m.find(i)) break
            i = m.start()
            val exprEnd = m.end()
            val argTerm = m.group(1)
            val modTerm = m.group(2)
            val typeTerm = m.group(3)
            var cookedArg: CharSequence
            when (typeTerm)
            {
                "%" ->
                {
                    cookedArg = "%"
                }
                "n" ->
                {
                    cookedArg = "\n"
                }
                else ->
                {
                    if (argTerm != null)
                    {
                        val argIdx: Int = when (argTerm)
                        {
                            "" -> ++argAt
                            "<" -> argAt
                            else -> argTerm.substring(0, argTerm.length - 1).toInt() - 1
                        }
                        val argItem = args[argIdx]
                        cookedArg = if (typeTerm == "s" && argItem is Spanned)
                        {
                            argItem
                        }
                        else
                        {
                            String.format(locale!!, "%$modTerm$typeTerm", argItem)
                        }
                    }
                    else
                    {
                        throw Exception("Wrong string, cannot format")
                    }
                }
            }
            out.replace(i, exprEnd, cookedArg)
            i += cookedArg.length
        }
        return SpannedString(out)
    }
}