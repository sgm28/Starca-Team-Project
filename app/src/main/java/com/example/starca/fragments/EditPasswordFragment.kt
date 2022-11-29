package com.example.starca.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.starca.R
import com.example.starca.SettingsActivity
import com.parse.ParseUser
import org.w3c.dom.Text

class EditPasswordDialogFragment : DialogFragment() {


    public interface EditPasswordDialogFragmentListener {
        fun onFinishedUpdatingPassword(string: String)
    }

    private lateinit var etOldPassword : EditText
    private lateinit var etNewPassword : EditText
    private lateinit var etConfirmPassword : EditText
    private lateinit var tvInvalidPasswordMessage: TextView


    public fun EditPasswordDialogFragment() {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etOldPassword = requireView().findViewById(R.id.etOldPassword)
        etNewPassword = requireView().findViewById(R.id.etNewPassword)
        etConfirmPassword = requireView().findViewById(R.id.etConfirmNewPassword)
        tvInvalidPasswordMessage = requireView().findViewById(R.id.tvInvalidPasswordMessage)


        etOldPassword.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        etNewPassword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
//                && isValidPassword(etNewPassword.text.toString()) &&
//                etConfirmPassword.text.toString() == etNewPassword.text.toString() &&
//                etOldPassword.text.toString() == ParseUser.getCurrentUser().getString("password") &&
//                etConfirmPassword.text.toString() != "" && etOldPassword.text.toString() != "" && etNewPassword.text.toString() != ""
            ) {
                val act = activity as EditPasswordDialogFragmentListener
                act.onFinishedUpdatingPassword(etNewPassword.text.toString())
                this.dismiss()
                true
            } else {
//                if (etOldPassword.text.toString() != ParseUser.getCurrentUser().getString("password")) {
//                    tvInvalidPasswordMessage.text = "Did not enter correct current password!"
//                    tvInvalidPasswordMessage.visibility = View.VISIBLE
//                }
//                else if (!(isValidPassword(etNewPassword.text.toString()))) {
//                    tvInvalidPasswordMessage.text = "Newly entered password does not fit the requirements!"
//                    tvInvalidPasswordMessage.visibility = View.VISIBLE
//                }
//                else
//                    if (etNewPassword.text.toString() != etConfirmPassword.text.toString()) {
//                    tvInvalidPasswordMessage.text = "New passwords don't match!"
//                    tvInvalidPasswordMessage.visibility = View.VISIBLE
//                }
                false
            }
        }

    }

//    fun isValidPassword(password: String): Boolean {
//        if (password.length < 8) return false
//        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
//        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
//        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
//        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false
//        if (!password.contains(",") && !password.contains("+") && !password.contains("^") && !password.contains("!")) return false
//
//        return true
//    }

    companion object {
        const val TAG = "EditPasswordDialog"

        public fun newInstance(title : String) : EditPasswordDialogFragment{
            val frag : EditPasswordDialogFragment = EditPasswordDialogFragment()
            val args : Bundle = Bundle()
            args.putString("title", title)
            frag.arguments = args
            return frag
        }
    }
}