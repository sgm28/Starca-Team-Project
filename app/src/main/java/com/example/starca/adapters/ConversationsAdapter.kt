package com.example.starca.adapters

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.starca.R
import com.example.starca.fragments.MessagingFragment
import com.example.starca.models.Conversation
import com.parse.ParseUser

const val CONVERSATION_BUNDLE = "CONVERSATION_BUNDLE"

class ConversationsAdapter(
    private val context: Context,
    private val conversations: List<Conversation>,
    private val blockList: ArrayList<String>,
    private val itemLongClickListener: OnItemLongClickListener
) : RecyclerView.Adapter<ConversationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = conversations[position]
        holder.bind(conversation)
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {

        private val tvRecipient: TextView
        private val ivRecipient: ImageView
        private val tvBlocked: TextView

        init {
            tvRecipient = itemView.findViewById(R.id.recipient_tv)
            ivRecipient = itemView.findViewById(R.id.conversations_profile_image_iv)
            tvBlocked = itemView.findViewById(R.id.tvBlocked)

            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        fun bind(conversation: Conversation) {
            // Load recipient data
            var recipient: ParseUser? = conversation.getYou()
            //Determines whether the user is the requester or the recipient
            if (recipient != null) {
                if (ParseUser.getCurrentUser().objectId == recipient.objectId) {
                    recipient = conversation.getOtherPerson()
                } else if (ParseUser.getCurrentUser().objectId == conversation.getOtherPerson()?.objectId) {
                    recipient = conversation.getYou()
                }
            }

            tvBlocked.visibility =
                if (blockList.contains(recipient?.objectId)) VISIBLE else INVISIBLE

            itemView.setBackgroundColor(if (blockList.contains(recipient?.objectId)) Color.parseColor("#CCCCCC") else Color.WHITE)

            tvRecipient.text =
                recipient?.getString("firstName") + " " + recipient?.getString("lastName")

            Glide.with(itemView.context)
                .load(conversation.getYou()?.getParseFile("profilePicture")?.url)
                .circleCrop()
                .into(ivRecipient)
        }


        override fun onClick(p0: View?) {

            Toast.makeText(context, "Clicked on a conversation", Toast.LENGTH_SHORT).show()
            val conversation = conversations[adapterPosition]

            // Send specific conversation object to messaging fragment
            val bundle = Bundle()
            bundle.putParcelable(CONVERSATION_BUNDLE, conversation)
            val messagingFragment = MessagingFragment()
            messagingFragment.arguments = bundle

            // Navigate to messaging fragment
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, messagingFragment)
                .addToBackStack(null)
                .commit()
        }

        override fun onLongClick(v: View): Boolean {
            itemLongClickListener.onItemLongClick(v, adapterPosition)
            return true
        }

    }

    interface OnItemLongClickListener {
        fun onItemLongClick(v: View, pos: Int)
    }
}