package com.example.starca.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.starca.R
import com.example.starca.adapters.ConversationsAdapter
import com.example.starca.models.Conversation
import com.parse.ParseQuery
import com.parse.ParseUser
import org.json.JSONArray

class ConversationsFragment : Fragment(), ConversationsAdapter.OnItemLongClickListener {

    lateinit var rvConversations: RecyclerView
    lateinit var adapter: ConversationsAdapter

    private val conversationsArrayList = ArrayList<Conversation>()

    lateinit var blockUsers_alertBuilder: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var you = ParseUser.getCurrentUser()

        var blockList = getBlockList(you)

//        Toast.makeText(context, "$blockList", Toast.LENGTH_SHORT).show()

        if (blockList == null) {
            blockList = ArrayList<String>()
            you.put(KEY_BLOCK_LIST, blockList)

            you.saveInBackground { e ->
                if (e == null) {
                    Log.i(TAG, "getBlockList: created (initialized) blocklist.")
                    init(view, blockList)
                } else {
                    Log.e(TAG, "getBlockList: $e")
                }
            }
        } else {
            init(view, blockList)
        }
    }

    private fun init(view: View, blocklistInit: ArrayList<String>) {

        rvConversations = view.findViewById(R.id.conversations_rv)
        adapter =
            ConversationsAdapter(requireContext(), conversationsArrayList, blocklistInit, this)
        rvConversations.adapter = adapter
        rvConversations.layoutManager = LinearLayoutManager(requireContext())
        blockUsers_alertBuilder = AlertDialog.Builder(context)

        queryConversations()
    }

    private fun queryConversations() {

        // Clear the list before writing data into it
        conversationsArrayList.clear()

        val query: ParseQuery<Conversation> = ParseQuery.getQuery(Conversation::class.java)

        query.include(Conversation.KEY_USER)
        query.include(Conversation.KEY_RECIPIENT)
        //Sakar: I change get KEY_USER to KEY_RECIPIENT so the user who sent the message is diplayed.
        query.whereEqualTo(Conversation.KEY_RECIPIENT, ParseUser.getCurrentUser())
        query.findInBackground { conversationsList, e ->
            if (e != null) {
                Log.e(TAG, "Error fetching posts ${e.message}")
            } else { //If empty list call, queryConversations2
                if (conversationsList.isEmpty()) {
                    queryConversations2()
                    return@findInBackground
                }

                if (conversationsList != null) {
                    Log.d("Conversations", conversationsList.toString())

                    for(singleConversationItem in conversationsList){
                        val otherPerson = singleConversationItem.getOtherPerson()
                        val cur_user = ParseUser.getCurrentUser().objectId

                        if(otherPerson?.objectId == ParseUser.getCurrentUser().objectId) {
                            continue
                        }

                        if (getBlockList(otherPerson) != null) {
                            // if other person's blocklist has you on it, u can't see anything.
                            if (getBlockList(otherPerson)!!.contains(cur_user)) {
                                continue
                            }
                        }
                        conversationsArrayList.add(singleConversationItem)
                    }

                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    //This function is call when the queryConversations returned an empty conversation list
    private fun queryConversations2() {

        // Clear the list before writing data into it
        conversationsArrayList.clear()

        val query: ParseQuery<Conversation> = ParseQuery.getQuery(Conversation::class.java)

        query.include(Conversation.KEY_USER)
        query.include(Conversation.KEY_RECIPIENT)
        //Sakar: I change get KEY_USER to KEY_RECIPENT so the user who sent the message is diplayed.
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

    override fun onItemLongClick(v: View, pos: Int) {
        // Function author: Drew

        if (v.findViewById<View?>(R.id.tvBlocked).visibility == INVISIBLE) {
            blockUser(pos, v)
        } else {
//            Toast.makeText(context, "Clicked Unblock", Toast.LENGTH_SHORT)
//                .show()
            unblockUser(pos, v)
        }

//        Toast.makeText(context, "Long clicked $pos", Toast.LENGTH_SHORT).show()

    }

    private fun blockUser(index: Int, v: View) {
        // Function author: Drew

        val user_to_block = conversationsArrayList[index].getOtherPerson()
        val userName = "${user_to_block?.getString("firstName")} ${user_to_block?.getString("lastName")}"

        val customDialog = layoutInflater.inflate(R.layout.custom_dialog, null)
        blockUsers_alertBuilder.setView(customDialog)

        val alert: AlertDialog = blockUsers_alertBuilder.create()
        alert.setCancelable(true)

        // Set up custom alert dialog
        alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.findViewById<TextView>(R.id.dialog_title).text = "Block $userName"
        customDialog.findViewById<TextView>(R.id.dialog_message).text = "Are you sure you would like to block $userName?"
        customDialog.findViewById<Button>(R.id.dialog_negative_button).setOnClickListener { alert.cancel() }
        customDialog.findViewById<Button>(R.id.dialog_positive_button).setOnClickListener {
            tryIgnore(user_to_block, v, true)
            alert.dismiss()
        }

        customDialog.findViewById<Button>(R.id.dialog_positive_button).setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.red))
        customDialog.findViewById<Button>(R.id.dialog_positive_button).text = "Block"
        alert.show()
    }

    private fun unblockUser(index: Int, v: View) {
        // Function author: Drew

        val user_to_block = conversationsArrayList[index].getOtherPerson()

        tryIgnore(user_to_block, v, false)
    }

    private fun tryIgnore(
        user_to_block: ParseUser?,
        v: View,
        setToAddIgnore: Boolean
    ) {
        // Function author: Drew

        val cur_user = ParseUser.getCurrentUser()

        var ignoreJsonArray = cur_user?.getJSONArray(KEY_BLOCK_LIST)
        // i want a list of blocked users.

        if (ignoreJsonArray == null) {
            ignoreJsonArray = JSONArray()
            cur_user?.put(KEY_BLOCK_LIST, ignoreJsonArray)
            cur_user?.saveInBackground {
                addIgnore(ignoreJsonArray, user_to_block, v)
            }
        } else {
            if (setToAddIgnore) {
                addIgnore(ignoreJsonArray, user_to_block, v)
            } else {
                removeIgnore(ignoreJsonArray, user_to_block, v)
            }
        }
    }

    private fun addIgnore(ignoreJsonArray: JSONArray, user_to_block: ParseUser?, v: View) {
        // Function author: Drew

        //okay. ignore list exists. convert to mutable list.
        val cur_user = ParseUser.getCurrentUser()

        val ignoreStrArray = ArrayList<String>()

        for (i in 0 until ignoreJsonArray.length()) {
            val userId_str = ignoreJsonArray.get(i).toString()
            ignoreStrArray.add(userId_str)
        }

        ignoreStrArray.add(user_to_block!!.objectId)

        cur_user?.put(KEY_BLOCK_LIST, ignoreStrArray)

        cur_user?.saveInBackground { e ->
            if (e == null) {

                val userName = "${user_to_block?.getString("firstName")} ${user_to_block?.getString("lastName")}"
                Toast.makeText(context, "Blocked $userName", Toast.LENGTH_SHORT)
                    .show()
                Log.i(TAG, "addIgnore: added user to ignore: ${user_to_block.objectId}")

                //okay, now change the way the button looks in the list.
                toggleBlockedVisibility(v, true)
            } else {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "addIgnore: $e")
            }
        }
    }

    private fun removeIgnore(ignoreJsonArray: JSONArray, user_to_block: ParseUser?, v: View) {
        // Function author: Drew

        //okay. ignore list exists. convert to mutable list.

        val cur_user = ParseUser.getCurrentUser()

        val ignoreStrArray = ArrayList<String>()

        for (i in 0 until ignoreJsonArray.length()) {
            val userId_str = ignoreJsonArray.get(i).toString()
            ignoreStrArray.add(userId_str)
        }

        ignoreStrArray.remove(user_to_block!!.objectId)

//        ignoreStrArray.clear()
//        Toast.makeText(context, "cleansed", Toast.LENGTH_SHORT).show()

        cur_user?.put(KEY_BLOCK_LIST, ignoreStrArray)

        cur_user?.saveInBackground { e ->
            if (e == null) {

                val userName = "${user_to_block?.getString("firstName")} ${user_to_block?.getString("lastName")}"
                Toast.makeText(context, "Unblocked $userName", Toast.LENGTH_SHORT)
                    .show()
                Log.i(TAG, "removeIgnore: removed user from ignore: ${user_to_block.objectId}")

                //okay, now change the way the button looks in the list.
                toggleBlockedVisibility(v, false)
            } else {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "addIgnore: $e")
            }
        }
    }


    private fun toggleBlockedVisibility(v: View, setBlockedOn: Boolean) {
        // Function author: Drew

        val vis = if (setBlockedOn) VISIBLE else INVISIBLE
        val col = if (setBlockedOn) Color.parseColor("#CCCCCC") else Color.WHITE

        val cur = v.findViewById<TextView>(R.id.tvBlocked)

        cur.visibility = vis
        v.setBackgroundColor(col)

    }

    private fun getBlockList(user: ParseUser?): ArrayList<String>? {

        var jsArray = user?.getJSONArray(KEY_BLOCK_LIST)

        var blockList = ArrayList<String>()

        if (jsArray == null) {
            return null
        }

        for (i in 0 until jsArray!!.length()) {
            blockList.add(jsArray[i].toString())
        }

        return blockList
    }

    companion object {
        const val KEY_BLOCK_LIST = "blockedUsers"

        val TAG = "ConversationsFragment"

    }
}