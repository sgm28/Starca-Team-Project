package com.example.starca.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.starca.R

class EditPasswordDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage("Change Password")
            .setPositiveButton("OK") {_, _ ->
                Toast.makeText(context, "Password Has Been Changed", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") {_,_ -> dismiss()}
            .setView(R.layout.fragment_edit_password)
            .create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        const val TAG = "EditPasswordDialog"
    }
}