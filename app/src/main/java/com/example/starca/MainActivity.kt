package com.example.starca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.starca.fragments.ConversationsFragment
import com.example.starca.fragments.CreateListingImageFragment
import com.example.starca.fragments.DashboardFragment
import com.example.starca.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.ParseUser

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

        // Bottom navigation view listener for switching between fragments
        findViewById<Toolbar>(R.id.toolbar).setOnMenuItemClickListener {
                item ->

            when (item.itemId) {
                R.id.toolbar_logout -> {
                    signOut()
                    goToLoginActivity()
                }
            }
            // This true signifies we handled the user interaction
            true
        }
    }

    private fun signOut(){
        Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
        ParseUser.logOut()
    }

    private fun goToLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}