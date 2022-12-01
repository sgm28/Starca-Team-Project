package com.example.starca.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.starca.R
import com.example.starca.models.Conversation

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val CONVERSATION_BUNDLE = "CONVERSATION_BUNDLE"

class MessagingFragment : Fragment() {
    private var conversation: Conversation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            conversation = it.getParcelable(CONVERSATION_BUNDLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messaging, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = conversation?.getUser()
        val recipient = conversation?.getRecipient()

        val messagingUserTv = view.findViewById<TextView>(R.id.messaging_user_tv)
        val messagingRecipientTv = view.findViewById<TextView>(R.id.messaging_recipient_tv)

        // TODO: Delete this. Load messages instead with this data
        messagingUserTv.text = "User: " +  user!!.getString("firstName")
        messagingRecipientTv.text = "Recipient: " + recipient!!.getString("firstName")
    }
}