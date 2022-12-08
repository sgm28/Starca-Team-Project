package com.example.starca.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.starca.MainActivity
import com.example.starca.R
import com.parse.ParseUser
import com.parse.RequestPasswordResetCallback

//TODO: Add progress bar when signing in or signing up
//TODO: Add transitions between sign in and sign up fragments (Object Animator(for cardview maybe)/shared element transition (for logo[or whatever the fragment equivalent is]))
class SignInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.sign_in_button).setOnClickListener {
            val username = view.findViewById<EditText>(R.id.sign_in_username_et).text.toString()
            val password = view.findViewById<EditText>(R.id.sign_in_password_et).text.toString()
            Log.i(TAG, "Login: U: $username P: $password")
            loginUser(username, password)
        }

        view.findViewById<TextView>(R.id.sign_up_tv).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.login_fragment_container, RegisterFragment()).addToBackStack(null).commit()
        }
    }

    private fun loginUser(username: String, password: String) {
        ParseUser.logInInBackground(username, password, ({ user, e ->
            if (user != null) {
                Log.i(TAG, "Successfully logged in user")
                goToMainActivity()
            } else {
                e.printStackTrace()
                showAlert("Login Failed", e.message + " Please try again")
            }
        }))
    }

    private fun goToMainActivity(){
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)

        // Finish activity we can't come back to the log in page by hitting back
        activity?.finish()
    }

    //TODO: Implement forgot password. Verify this function works
    private fun forgotPassword(email: String){
        ParseUser.requestPasswordResetInBackground(email, RequestPasswordResetCallback { e ->
            if (e != null) {
                // Email sent. Notify user
            } else {
                // Handle exception
            }
        })
    }

    //TODO: change request screen to say "requests for {myListing}" and delete the location and name from each item
    //      allow user to edit their listing
    //      allow users to rate the listing (in rental detail fragment)
    //      change conversations to display correct image of user
    //      check conversations and messaging
    //      edit profile styling
    //      amenities (add a price field here)
    //      creation styling

    private fun showAlert(title: String, message: String){
        val builder = AlertDialog.Builder(requireContext())

        val customDialog = layoutInflater.inflate(R.layout.custom_message_dialog, null)
        builder.setView(customDialog)
        val alert = builder.create()

        // Set up custom alert error dialog
        alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.findViewById<TextView>(R.id.message_dialog_title).text = title
        customDialog.findViewById<TextView>(R.id.message_dialog_message).text = message
        customDialog.findViewById<Button>(R.id.message_dialog_positive_button).setOnClickListener {
            alert.dismiss()
        }

        alert.show()
    }

    companion object {
        const val TAG = "LoginActivity-SignInFragment"
    }
}