package com.example.starca.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.starca.R
import com.parse.GetCallback
import com.parse.Parse
import com.parse.ParseException
import com.parse.ParseUser

class EditUsernameDialogFragment : DialogFragment() {

//    fun newInstance(user : ParseUser) : EditUsernameDialogFragment {
//        val args = Bundle()
//        val frag = EditUsernameDialogFragment()
//
//        val fragment = ()
//        fragment.arguments = args
//        return fragment
//    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage("Change Username")
            .setPositiveButton("OK") {_,_ ->
                val query = ParseUser.getQuery()
                query.whereEqualTo("objectId", ParseUser.getCurrentUser().objectId)
                query.getFirstInBackground(object: GetCallback<ParseUser>{
                    override fun done(user: ParseUser, e: ParseException?) {
                        if (e == null) {
                            user.put(
                                "username",
                                view?.findViewById<EditText>(R.id.etNewUsername)?.text.toString()
                            )
                            user.saveInBackground()
                        }
                    }
                })
            }
            .setNegativeButton("Cancel") {_,_ ->dismiss()}
            .setView(R.layout.fragment_edit_username)
            .create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_username, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        const val TAG = "EditUsernameDialog"
    }
}