package com.myniprojects.fuelmanager.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.databinding.ActivityMainBinding
import com.myniprojects.fuelmanager.ui.car.CarFragment
import com.myniprojects.fuelmanager.utils.Log


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{

    private lateinit var viewModel: MainActivityVM
    private lateinit var drawerLayout: DrawerLayout

    companion object
    {
        var darkThemeStyle: Boolean = false
            private set

        var currency: String = "$"
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val darkTheme = sharedPref.getBoolean(CarFragment.THEME_KEY, false)


        //setup theme
        darkThemeStyle = darkTheme

        if (darkTheme) // dark
        {
            Log.d("Dark")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            delegate.applyDayNight()
        }
        else // day
        {
            Log.d("Day")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            delegate.applyDayNight()
        }

        //setup view model
        viewModel = ViewModelProvider(this).get(MainActivityVM::class.java)

        // setup navigation bar and navigation drawer
        drawerLayout = binding.drawerLayout

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        setObserversToSettings()

        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun setObserversToSettings()
    {
        viewModel.currency.observe(this, Observer {
            currency = it
            Log.d("Currency changed to: $it")
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.aboutFragment, R.id.settingsFragment, R.id.statisticFragment ->
            {
                drawerLayout.close()
                NavigationUI.onNavDestinationSelected(
                    item,
                    findNavController(R.id.navHostFragment)
                )
            }

            R.id.ourApps ->
            {
                val browserIntent =
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=MyniProjects")
                    )
                if (null == browserIntent.resolveActivity(packageManager))
                {
                    Toast.makeText(
                        this,
                        getString(R.string.cannot_open_our_apps),
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