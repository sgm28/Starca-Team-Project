package com.example.starca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.starca.fragments.ConversationsFragment
import com.example.starca.fragments.CreateListingImageFragment
import com.example.starca.fragments.DashboardFragment
import com.example.starca.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager

        // Displaying dashboard fragment once user has signed in
        fragmentManager.beginTransaction().replace(R.id.fragment_container, DashboardFragment()).commit()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.inflateMenu(R.menu.menu)

        // Bottom navigation view listener for switching between fragments
        findViewById<BottomNavigationView>(R.id.bottom_nav_bar).setOnItemSelectedListener {
            item ->

            var currentFragment: Fragment? = null
            when (item.itemId) {
                R.id.bottom_nav_dashboard -> {
                    currentFragment = DashboardFragment()
                }
                R.id.bottom_nav_create -> {
                    currentFragment = CreateListingImageFragment()
                }
                R.id.bottom_nav_conversations -> {
                    currentFragment = ConversationsFragment()
                }
                R.id.bottom_nav_profile -> {
                    currentFragment = ProfileFragment()
                }
            }
            if (currentFragment != null) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, currentFragment).commit()
            }
            // This true signifies we handled the user interaction
            true
        }
    }

    fun signOut(){
        //TODO: Sign out parse user on toolbar logout button click
    }
}