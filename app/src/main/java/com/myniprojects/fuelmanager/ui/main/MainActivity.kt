package com.myniprojects.fuelmanager.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.databinding.ActivityMainBinding
import com.myniprojects.fuelmanager.utils.Log


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?)
    {
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

        binding.navView.setNavigationItemSelectedListener(this)
//        ContextCompat.getColor(this, R.color.colorPrimaryDark)

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
            R.id.aboutFragment ->
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
                Log.d("ELSE")
                false
            }
        }
    }

}