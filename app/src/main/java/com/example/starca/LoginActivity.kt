package com.example.starca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.login_button).setOnClickListener {
            goToMainActivity()
        }
    }

    private fun registerUser(){
        // TODO: finish setting up registration
    }

    private fun loginUser(){
        // TODO: finish setting up login
    }

    private fun goToMainActivity(){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)

        // Finish activity we can't come back to the log in page by hitting back
        finish()
    }
}