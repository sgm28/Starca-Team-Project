package com.example.starca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.starca.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.ParseUser

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Send user straight to MainActivity if they are already signed in
        if (ParseUser.getCurrentUser() != null) {
            goToMainActivity()
        }

        val fragmentManager: FragmentManager = supportFragmentManager

        // Display the login fragment when user enters the app
        fragmentManager.beginTransaction().replace(R.id.login_fragment_container, SignInFragment()).commit()

        // Bottom navigation view listener for switching between fragments
        findViewById<BottomNavigationView>(R.id.login_bottom_nav_bar).setOnItemSelectedListener {
                item ->

            var currentFragment: Fragment? = null
            when (item.itemId) {
                R.id.login_bottom_nav_sign_in -> {
                    currentFragment = SignInFragment()
                }
                R.id.login_bottom_nav_register -> {
                    currentFragment = RegisterFragment()
                }
            }
            if (currentFragment != null) {
                fragmentManager.beginTransaction().replace(R.id.login_fragment_container, currentFragment).commit()
            }
            // This true signifies we handled the user interaction
            true
        }
    }

    private fun goToMainActivity(){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)

        // Finish activity so we can't come back to the log in page by hitting back
        this.finish()
    }
}