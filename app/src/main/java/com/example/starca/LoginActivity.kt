package com.example.starca

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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

        showCustomUI()
        val fragmentManager: FragmentManager = supportFragmentManager

        // Display the login fragment when user enters the app
        fragmentManager.beginTransaction().replace(R.id.login_fragment_container, SignInFragment()).commit()

    }

    private fun goToMainActivity(){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)

        // Finish activity so we can't come back to the log in page by hitting back
        this.finish()
    }

    //TODO: replace depracated method
    //Allows background to display on the status bar
    private fun showCustomUI() {
        val decorView: View = window.decorView
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )
    }
}