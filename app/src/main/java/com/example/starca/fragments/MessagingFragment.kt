package com.example.starca.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.starca.R
import com.example.starca.adapters.ChatAdapter
import com.example.starca.models.Conversation
import com.example.starca.models.Message
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val CONVERSATION_BUNDLE = "CONVERSATION_BUNDLE"







class MessagingFragment : Fragment() {



    //For loading messages
    val TAG: String? = "MessagingFragment"
    val USER_ID_KEY = "userId"
    val BODY_KEY = "body"
    val MAX_CHAT_MESSAGES_TO_SHOW = 50
    var etMessage: EditText? = null
    var ibSend: ImageButton? = null
    var rvChat: RecyclerView? = null

    var mMessages: ArrayList<Message>? = null
    var mFirstLoad = false
    var mAdapter: ChatAdapter? = null


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
        return inflater.inflate(R.layout.activity_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = conversation?.getUser()
        val recipient = conversation?.getRecipient()

        val messagingUserTv = view.findViewById<TextView>(R.id.messaging_user_tv)
        val messagingRecipientTv = view.findViewById<TextView>(R.id.messaging_recipient_tv)

        /*
        // TODO: Delete this. Load messages instead with this data
        messagingUserTv.text = "User: " +  user!!.getString("firstName")
        messagingRecipientTv.text = "Recipient: " + recipient!!.getString("firstName")
         */


        etMessage = view.findViewById<View>(R.id.etMessage) as EditText
        ibSend = view.findViewById<View>(R.id.ibSend) as ImageButton
        rvChat = view.findViewById<View>(R.id.rvChat) as RecyclerView
        mMessages = java.util.ArrayList()
        mFirstLoad = true
        val userId = ParseUser.getCurrentUser().objectId
        Log.d(TAG,userId)
        mAdapter = ChatAdapter(requireContext(), userId, mMessages)
        rvChat!!.adapter = mAdapter

        // associate the LayoutManager with the RecylcerView

        // associate the LayoutManager with the RecylcerView
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        rvChat!!.layoutManager = linearLayoutManager
        val owner = recipient!!.getString("firstName")
        refreshMessages()
        setupMessagePosting(view)
    }


    // Query messages from Parse so we can load them into the chat adapter
    // Query messages from Parse so we can load them into the chat adapter
    // Query the conversation to get the user and recipent - DONE
    fun refreshMessages() {
















        // Construct query to execute
        val query = ParseQuery.getQuery(Message::class.java)
        // Configure limit and sort order
        query.limit = MAX_CHAT_MESSAGES_TO_SHOW

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt")
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.whereContains("conversationId", conversation?.objectId)

        query.include(Message.KEY_CONVERSATION)

        query.findInBackground { messages, e ->
            if (e == null) {
       //         Log.i(TAG, messages.toString())

//                for (message in messages){
//                    Log.d("Message", message.getBody()!!)
//                }


                mMessages!!.clear()
//                val user= mMessages!!.get(0).getConversation()
//                messages.get(0).setUserId(conversation?.getRecipient().toString())
//                val recipent = conversation?.getUser()?.getString("firstName")
//                for (s in messages)
//                    if (s.getUserId()?.isEmpty() == true) {
//                        recipent?.let { s.setUserId(it) }
//                    }
                mMessages!!.addAll(messages)
                mAdapter!!.notifyDataSetChanged() // update adapter
                // Scroll to the bottom of the list on initial load
                if (mFirstLoad) {
                    rvChat!!.scrollToPosition(0)
                    mFirstLoad = false
                }
            } else {
                Log.e("message", "Error Loading Messages$e")
            }
        }
    }

    // Set up button event handler which posts the entered message to Parse
    // Setup message field and posting
    // Setup message field and posting
    fun setupMessagePosting(view: View) {
        etMessage = view.findViewById<View>(R.id.etMessage) as EditText
        ibSend = view.findViewById<View>(R.id.ibSend) as ImageButton
        rvChat = view.findViewById<View>(R.id.rvChat) as RecyclerView
        mMessages = java.util.ArrayList()
        mFirstLoad = true
        val userId = ParseUser.getCurrentUser().objectId
        mAdapter = ChatAdapter(context, userId, mMessages)
        rvChat!!.adapter = mAdapter

        // associate the LayoutManager with the RecylcerView
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        rvChat!!.layoutManager = linearLayoutManager

        // When send button is clicked, create message object on Parse
        ibSend!!.setOnClickListener {
            val data = etMessage!!.text.toString()
            //ParseObject message = ParseObject.create("Message");
            //message.put(Message.USER_ID_KEY, userId);
            //message.put(Message.BODY_KEY, data);
            // Using new `Message` Parse-backed model now
            val message = Message()
            conversation?.let { it1 -> message.setConversation(it1) }
            message.setBody(data)
            message.setUserId(ParseUser.getCurrentUser().objectId)

            message.saveInBackground {
                Toast.makeText(
                    context, "Successfully created message on Parse",
                    Toast.LENGTH_SHORT
                ).show()
                refreshMessages()
            }
            etMessage!!.setText(null)
        }
    }

    // Create a handler which can run code periodically
    val POLL_INTERVAL = TimeUnit.SECONDS.toMillis(3)
    var myHandler = Handler()
    var mRefreshMessagesRunnable: Runnable = object : Runnable {
        override fun run() {
            refreshMessages()
            myHandler.postDelayed(this, POLL_INTERVAL)
        }
    }

    override fun onResume() {
        super.onResume()

        // Only start checking for new messages when the app becomes active in foreground
        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL)
    }

    override fun onPause() {
        // Stop background task from refreshing messages, to avoid unnecessary traffic & battery drain
        myHandler.removeCallbacksAndMessages(null)
        super.onPause()
    }


}