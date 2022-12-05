package com.example.starca.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.example.starca.R
import com.example.starca.SettingsActivity
import com.parse.Parse
import com.parse.ParseException
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
    private val userName = ParseUser.getCurrentUser().username.toString()

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

        etConfirmPassword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE && allRequirementsMet()) {
                val act = activity as EditPasswordDialogFragmentListener
                act.onFinishedUpdatingPassword(etNewPassword.text.toString())
                this.dismiss()
                true
            } else {
                false
            }
        }

        etOldPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(password: CharSequence?, p1: Int, p2: Int, p3: Int) {
                ParseUser.logInInBackground(userName, password.toString()) {parseUser: ParseUser?, e: ParseException? ->
                    if (parseUser == null) {
                        tvInvalidPasswordMessage.text = "Incorrect Current Password Entered"
                        tvInvalidPasswordMessage.visibility = View.VISIBLE
                        etOldPassword.setTextColor(Color.RED)
                    } else {
                        tvInvalidPasswordMessage.visibility = View.INVISIBLE
                        etOldPassword.setTextColor(Color.BLACK)
                    }
                }
            }
            override fun onTextChanged(password: CharSequence?, p1: Int, p2: Int, p3: Int) {
                ParseUser.logInInBackground(userName, password.toString()) {parseUser: ParseUser?, e: ParseException? ->
                    if (parseUser == null) {
                        tvInvalidPasswordMessage.text = "Incorrect Current Password Entered"
                        tvInvalidPasswordMessage.visibility = View.VISIBLE
                        etOldPassword.setTextColor(Color.RED)
                    } else {
                        tvInvalidPasswordMessage.visibility = View.INVISIBLE
                        etOldPassword.setTextColor(Color.BLACK)
                    }
                }
            }
            override fun afterTextChanged(password: Editable?) {
                ParseUser.logInInBackground(userName, password.toString()) {parseUser: ParseUser?, e: ParseException? ->
                    if (parseUser == null) {
                        tvInvalidPasswordMessage.text = "Incorrect Current Password Entered"
                        tvInvalidPasswordMessage.visibility = View.VISIBLE
                        etOldPassword.setTextColor(Color.RED)
                    } else {
                        tvInvalidPasswordMessage.visibility = View.INVISIBLE
                        etOldPassword.setTextColor(Color.BLACK)
                    }
                }
            }
        })

        etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(password: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!isValidPassword(etNewPassword.text.toString())) {
                    tvInvalidPasswordMessage.text = "-New password missing one of:\n" +
                            "- At least one uppercase character\n" +
                            "- At least one lowercase letter\n" +
                            "- At least one digit\n" +
                            "- At least 8 characters long\n" +
                            "- At least one of: ',' '!' '+' '^'"
                    tvInvalidPasswordMessage.visibility = View.VISIBLE
                    etNewPassword.setTextColor(Color.RED)
                    etConfirmPassword.setTextColor(Color.RED)
                } else {
                    tvInvalidPasswordMessage.visibility = View.INVISIBLE
                    etNewPassword.setTextColor(Color.BLACK)
                    etConfirmPassword.setTextColor(Color.BLACK)
                }
            }
            override fun onTextChanged(password: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!isValidPassword(etNewPassword.text.toString())) {
                    tvInvalidPasswordMessage.text = "-New password missing one of:\n" +
                            "- At least one uppercase character\n" +
                            "- At least one lowercase letter\n" +
                            "- At least one digit\n" +
                            "- At least 8 characters long\n" +
                            "- At least one of: ',' '!' '+' '^'"
                    tvInvalidPasswordMessage.visibility = View.VISIBLE
                    etNewPassword.setTextColor(Color.RED)
                    etConfirmPassword.setTextColor(Color.RED)
                } else {
                    tvInvalidPasswordMessage.visibility = View.INVISIBLE
                    etNewPassword.setTextColor(Color.BLACK)
                    etConfirmPassword.setTextColor(Color.BLACK)
                }
            }
            override fun afterTextChanged(password: Editable?) {
                if (!isValidPassword(etNewPassword.text.toString())) {
                    tvInvalidPasswordMessage.text = "-New password missing one of:\n" +
                            "- At least one uppercase character\n" +
                            "- At least one lowercase letter\n" +
                            "- At least one digit\n" +
                            "- At least 8 characters long\n" +
                            "- At least one of: ',' '!' '+' '^'"
                    tvInvalidPasswordMessage.visibility = View.VISIBLE
                    etNewPassword.setTextColor(Color.RED)
                    etConfirmPassword.setTextColor(Color.RED)
                } else {
                    tvInvalidPasswordMessage.visibility = View.INVISIBLE
                    etNewPassword.setTextColor(Color.BLACK)
                    etConfirmPassword.setTextColor(Color.BLACK)
                }
            }
        })

        etConfirmPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (etConfirmPassword.text.toString() != etNewPassword.text.toString()) {
                    tvInvalidPasswordMessage.text = "New Passwords Do Not Match"
                    tvInvalidPasswordMessage.visibility = View.VISIBLE
                    etNewPassword.setTextColor(Color.RED)
                    etConfirmPassword.setTextColor(Color.RED)
                } else {
                    tvInvalidPasswordMessage.visibility = View.INVISIBLE
                    etNewPassword.setTextColor(Color.BLACK)
                    etConfirmPassword.setTextColor(Color.BLACK)
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (etConfirmPassword.text.toString() != etNewPassword.text.toString()) {
                    tvInvalidPasswordMessage.text = "New Passwords Do Not Match"
                    tvInvalidPasswordMessage.visibility = View.VISIBLE
                    etNewPassword.setTextColor(Color.RED)
                    etConfirmPassword.setTextColor(Color.RED)
                } else {
                    tvInvalidPasswordMessage.visibility = View.INVISIBLE
                    etNewPassword.setTextColor(Color.BLACK)
                    etConfirmPassword.setTextColor(Color.BLACK)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (etConfirmPassword.text.toString() != etNewPassword.text.toString()) {
                    tvInvalidPasswordMessage.text = "New Passwords Do Not Match"
                    tvInvalidPasswordMessage.visibility = View.VISIBLE
                    etNewPassword.setTextColor(Color.RED)
                    etConfirmPassword.setTextColor(Color.RED)
                } else {
                    tvInvalidPasswordMessage.visibility = View.INVISIBLE
                    etNewPassword.setTextColor(Color.BLACK)
                    etConfirmPassword.setTextColor(Color.BLACK)
                }
            }

        })
    }

    fun allRequirementsMet() : Boolean {
        var correctCurrentPassword = true

        ParseUser.logInInBackground(ParseUser.getCurrentUser().username.toString(), etOldPassword.text.toString()) { parseUser: ParseUser?, e: ParseException? ->
            if (parseUser == null) {
                correctCurrentPassword = false
            }
        }

        if (!correctCurrentPassword) {
            return false
        } else if (etNewPassword.text.toString() != etConfirmPassword.text.toString()) {
            return false
        } else if (!isValidPassword(etNewPassword.text.toString())) {
            return false
        }

        return true
    }

    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
//        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false
        if (!password.contains(",") && !password.contains("+") && !password.contains("^") && !password.contains("!")) return false

        return true
    }

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