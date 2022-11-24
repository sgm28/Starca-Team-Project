package com.example.starca.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
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
import org.w3c.dom.Text

class EditUsernameDialogFragment : DialogFragment() {


    public interface EditUsernameDialogFragmentListener {
        fun onFinishedUpdatingUsername(string: String)
    }

    private lateinit var etNewUsername : EditText

    public fun EditUsernameDialogFragment() {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_username, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etNewUsername = requireView().findViewById(R.id.etNewUsername)

        etNewUsername.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        etNewUsername.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val act = activity as EditUsernameDialogFragmentListener
                act.onFinishedUpdatingUsername(etNewUsername.text.toString())
                this.dismiss()
                true
            } else {
                false
            }
        }

    }

    companion object {
        const val TAG = "EditUsernameDialog"

        public fun newInstance(title : String) : EditUsernameDialogFragment{
            val frag : EditUsernameDialogFragment = EditUsernameDialogFragment()
            val args : Bundle = Bundle()
            args.putString("title", title)
            frag.arguments = args
            return frag
        }
    }
}