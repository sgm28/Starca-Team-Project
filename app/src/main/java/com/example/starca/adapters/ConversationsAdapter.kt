package com.example.starca.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
    private val conversations: List<Conversation>
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

    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val tvRecipient: TextView
        private val ivRecipient: ImageView

        init {
            tvRecipient = itemView.findViewById(R.id.recipient_tv)
            ivRecipient = itemView.findViewById(R.id.conversations_profile_image_iv)

            itemView.setOnClickListener(this)
        }

        fun bind(conversation: Conversation) {
            // Load recipient data
            var recipient: ParseUser? = conversation.getRecipient()
            //Determines whether the user is the requester or the recipient
            if (recipient != null) {
                if(ParseUser.getCurrentUser().objectId == recipient.objectId) {
                    recipient = conversation.getUser()
                } else if (ParseUser.getCurrentUser().objectId == conversation.getUser()?.objectId  ) {
                    recipient = conversation.getRecipient()
                }
            }
            tvRecipient.text = recipient?.getString("firstName") + " " + recipient?.getString("lastName")

            Glide.with(itemView.context)
                .load(conversation.getRecipient()?.getParseFile("profilePicture")?.url)
                .circleCrop()
                .into(ivRecipient)
        }


        override fun onClick(p0: View?) {

            Toast.makeText(context,"Clicked on a conversation", Toast.LENGTH_SHORT).show()
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
    }
}