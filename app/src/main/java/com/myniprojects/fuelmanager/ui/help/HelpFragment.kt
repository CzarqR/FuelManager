package com.myniprojects.fuelmanager.ui.help

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.myniprojects.fuelmanager.R
import kotlinx.android.synthetic.main.fragment_help.*


class HelpFragment : Fragment()
{
    companion object
    {
        const val PADDING_HELP_TEXT = 6
        const val SEPARATOR_MARGIN = 10
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val arrayTitles: Array<String> = resources.getStringArray(R.array.help_titles)
        val arrayContent: Array<String> = resources.getStringArray(R.array.help_content)

        val padding = (resources.displayMetrics.density * PADDING_HELP_TEXT).toInt()
        val margin = (resources.displayMetrics.density * SEPARATOR_MARGIN).toInt()

        for (i in arrayTitles.indices)
        {
            val helpText = TextView(requireContext())
            helpText.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                Html.fromHtml(
                    getString(R.string.help_text_format, arrayTitles[i], arrayContent[i]),
                    Html.FROM_HTML_MODE_LEGACY
                )
            }
            else
            {
                HtmlCompat.fromHtml(
                    getString(R.string.help_text_format, arrayTitles[i], arrayContent[i]),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                helpText.setTextAppearance(R.style.GreenText)
            }
            else
            {
                TextViewCompat.setTextAppearance(helpText, R.style.GreenText)
            }

            helpText.setPadding(
                padding, padding, padding, padding
            )

            linLayHelpContent.addView(helpText)

            if (i != arrayTitles.lastIndex) //check if it not last element, if so don't add separator
            {
                val separator = View(requireContext(), null, 0, R.style.Divider)

                val dividerHeight = resources.displayMetrics.density * 1 // 1dp to pixels

                val lp = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dividerHeight.toInt()
                )

                lp.marginEnd = margin
                lp.marginStart = margin
                separator.layoutParams = lp

                linLayHelpContent.addView(separator)
            }

        }
    }

}