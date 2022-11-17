package com.example.starca.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.starca.MainActivity
import com.example.starca.R
import com.parse.ParseUser

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<Button>(R.id.register_button).setOnClickListener {
            val firstName = view.findViewById<EditText>(R.id.first_name_et).text.toString()
            val lastName = view.findViewById<EditText>(R.id.last_name_et).text.toString()
            val email = view.findViewById<EditText>(R.id.email_et).text.toString()
            val username = view.findViewById<EditText>(R.id.username_et).text.toString()
            val password = view.findViewById<EditText>(R.id.password_et).text.toString()

            registerUser(firstName, lastName, email, username, password)
        }
    }

    private fun registerUser(firstName: String, lastName: String, email: String, username: String, password: String) {

        val user = ParseUser()

        user.put("firstName", firstName)
        user.put("lastName", lastName)
        user.put("email", email)
        user.username = username
        user.setPassword(password)

        user.signUpInBackground { e ->
            if (e == null) {
                // User successfully created new account
                Log.i(TAG, "Successfully registered!")
                goToMainActivity()
            } else {
                Toast.makeText(requireContext(), "Error registering your account", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun goToMainActivity(){
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)

        // Finish activity so we can't come back to the log in page by hitting back
        activity?.finish()
    }

    companion object {
        const val TAG = "LoginActivity-RegisterFragment"
    }
}