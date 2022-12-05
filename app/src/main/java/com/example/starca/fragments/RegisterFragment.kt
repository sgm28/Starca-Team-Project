package com.example.starca.fragments

import android.app.AlertDialog
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
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
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

        // Handle back press to go back to the SignIn Fragment
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        })
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
                ParseUser.logOut()
                //goToMainActivity()
                showAlert("Successfully Registered!", "Please verify your email before signing in.", false)
            } else {
                Toast.makeText(requireContext(), "Error registering your account", Toast.LENGTH_SHORT).show()
                // TODO: Show more user friendly error message
                showAlert("Error registering your account", "Failed to register :" + e.message, true)
                e.printStackTrace()
            }
        }
    }

    private fun showAlert(title: String, message: String, error: Boolean){
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, which ->
                dialog.cancel()

                if (!error) {
                    //TODO: Add transition here
                    parentFragmentManager.beginTransaction().replace(R.id.login_fragment_container, SignInFragment()).commit()
                }
            }
        val alert = builder.create()
        alert.show()
    }

    companion object {
        const val TAG = "LoginActivity-RegisterFragment"
    }
}