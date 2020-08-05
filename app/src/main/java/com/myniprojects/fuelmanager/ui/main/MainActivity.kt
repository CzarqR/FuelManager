package com.myniprojects.fuelmanager.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.databinding.ActivityMainBinding
import com.myniprojects.fuelmanager.utils.Log
import com.myniprojects.fuelmanager.utils.hideKeyboard


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{

    private lateinit var viewModel: MainActivityVM
    private lateinit var drawerLayout: DrawerLayout

    companion object
    {


        const val THEME_KEY: String = "THEME_KEY"
        const val CURRENCY_KEY: String = "CURRENCY_KEY"
        const val LENGTH_UNIT_KEY: String = "LENGTH_UNIT_KEY"
        const val VOLUME_UNIT_KEY: String = "VOLUME_UNIT_KEY"

        const val DEFAULT_DARK_THEME: Boolean = false

        lateinit var currency: String
            private set

        lateinit var lengthUnit: String
            private set

        lateinit var volumeUnit: String
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        //setup view model
        viewModel = ViewModelProvider(this).get(MainActivityVM::class.java)
        setObserversToSettings()

        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)


        // setup navigation bar and navigation drawer
        drawerLayout = binding.drawerLayout

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        findViewById<View>(android.R.id.content).isFocusableInTouchMode = true

        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun setObserversToSettings()
    {
        viewModel.darkTheme.observe(this, Observer {
            if (it) // dark
            {
                Log.d("Theme changed to Dark")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
            }
            else // day
            {
                Log.d("Theme changed to Day")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
            }
        })

        viewModel.currency.observe(this, Observer {
            currency = it
            Log.d("Currency changed to: $it")
        })

        viewModel.volumeUnit.observe(this, Observer {
            volumeUnit = it
            Log.d("volumeUnit changed to: $it")
        })

        viewModel.lengthUnit.observe(this, Observer {
            lengthUnit = it
            Log.d("lengthUnit changed to: $it")
        })
    }

    override fun onSupportNavigateUp(): Boolean
    {
        return if (drawerLayout.isOpen)
        {
            drawerLayout.close()
            true
        }
        else
        {
            val navController = this.findNavController(R.id.navHostFragment)
            NavigationUI.navigateUp(navController, drawerLayout)


        }
    }


    // region deactivate keyboard after clicking outside EditText
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean
    {
        val v: View? = currentFocus
        if (v != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
            v is EditText
        )
        {
            val sourceCoordinates = IntArray(2)
            v.getLocationOnScreen(sourceCoordinates)
            val x: Float = ev.rawX + v.getLeft() - sourceCoordinates[0]
            val y: Float = ev.rawY + v.getTop() - sourceCoordinates[1]
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
            {
                hideKeyboard()
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    // endregion

    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.aboutFragment, R.id.settingsFragment, R.id.statisticFragment ->
            {
                drawerLayout.close()

                return NavigationUI.onNavDestinationSelected(
                    item,
                    findNavController(R.id.navHostFragment)
                )

                val builder = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setEnterAnim(R.anim.enter_left_to_right)
                    .setExitAnim(R.anim.exit_right_to_left)
                    .setPopEnterAnim(R.anim.popenter_right_to_left)
                    .setPopExitAnim(R.anim.popexit_left_to_right)


                val options = builder.build()
                return try
                {
                    findNavController(R.id.navHostFragment).navigate(item.itemId, null, options)
                    true
                } catch (e: IllegalArgumentException)
                {
                    false
                }

            }

            R.id.ourApps ->
            {
                val browserIntent =
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.app_page))
                    )
                if (null == browserIntent.resolveActivity(packageManager))
                {
                    Toast.makeText(
                        this,
                        getString(R.string.app_page),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else
                {
                    startActivity(browserIntent)
                }
                true
            }
            else ->
            {
                Log.d("Unsupported options in nav drawer menu")
                false
            }
        }
    }


}