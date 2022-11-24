package com.example.starca.fragments


import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.starca.R

class EditEmailDialogFragment : DialogFragment() {

    public interface EditEmailDialogFragmentListener {
        fun onFinishedUpdatingEmail(string: String)
    }

    private lateinit var etNewEmail : EditText

    public fun EditEmailDialogFragment() {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etNewEmail = requireView().findViewById(R.id.etNewEmail)

        etNewEmail.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        etNewEmail.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE && isValidEmail(etNewEmail.text.toString())) {
                val act = activity as EditEmailDialogFragmentListener
                act.onFinishedUpdatingEmail(etNewEmail.text.toString())
                this.dismiss()
                true
            } else {
                etNewEmail.setTextColor(Color.parseColor("#FF0000"))
                view.findViewById<TextView>(R.id.tvInvalidEmailFormatMessage).visibility = View.VISIBLE
                false
            }
        }
    }

    fun isValidEmail(email: String) : Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    companion object {
        const val TAG = "EditEmailDialog"

        public fun newInstance(title : String) : EditEmailDialogFragment{
            val frag : EditEmailDialogFragment = EditEmailDialogFragment()
            val args : Bundle = Bundle()
            args.putString("title", title)
            frag.arguments = args
            return frag
        }
    }
}