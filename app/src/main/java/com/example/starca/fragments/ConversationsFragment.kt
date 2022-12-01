package com.example.starca.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.starca.R
import com.example.starca.adapters.ConversationsAdapter
import com.example.starca.models.Conversation
import com.parse.ParseQuery
import com.parse.ParseUser

class ConversationsFragment : Fragment() {

    lateinit var rvConversations: RecyclerView
    lateinit var adapter: ConversationsAdapter
    val conversationsArrayList = ArrayList<Conversation>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvConversations = view.findViewById(R.id.conversations_rv)
        adapter = ConversationsAdapter(requireContext(), conversationsArrayList)
        rvConversations.adapter = adapter
        rvConversations.layoutManager = LinearLayoutManager(requireContext())

        queryConversations()
    }

    private fun queryConversations() {

        // Clear the list before writing data into it
        conversationsArrayList.clear()

        val query: ParseQuery<Conversation> = ParseQuery.getQuery(Conversation::class.java)

        query.include(Conversation.KEY_USER)
        query.include(Conversation.KEY_RECIPIENT)
        query.whereEqualTo(Conversation.KEY_USER, ParseUser.getCurrentUser())
        query.findInBackground { conversationsList, e ->
            if (e != null) {
                Log.e(DashboardFragment.TAG, "Error fetching posts ${e.message}")
            } else {
                if (conversationsList != null) {
                    Log.d("Conversations", conversationsList.toString())
                    conversationsArrayList.addAll(conversationsList)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}