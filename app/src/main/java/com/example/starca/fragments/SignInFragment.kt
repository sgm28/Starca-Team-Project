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
import com.example.starca.MainActivity
import com.example.starca.R
import com.parse.ParseUser

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
            loginUser(username, password)
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

    private fun showAlert(title: String, message: String){
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, which ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }

    companion object {
        const val TAG = "LoginActivity-SignInFragment"
    }
}