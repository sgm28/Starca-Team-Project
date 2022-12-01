package com.example.starca.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.starca.R
import com.example.starca.fragments.OwnerListingDetailFragment
import com.example.starca.ListingRequest
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser
import de.hdodenhof.circleimageview.CircleImageView

class RequestsAdapter(val context: Context, val requests: ArrayList<ListingRequest>) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestsAdapter.ViewHolder, position: Int) {
        val request = requests[position]
        holder.bind(request)
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    fun clear() {
        requests.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivRequesterProfilePhoto : CircleImageView
        val tvRequestersName : TextView
        val tvRequestedListingTitle : TextView
        val tvRequestedListingAddress : TextView
        val rbRequesterRating : RatingBar
        val buttonRejectRequest : Button
        val buttonAcceptRequest : Button

        init {
            ivRequesterProfilePhoto = itemView.findViewById(R.id.ivRequesterProfilePhoto)
            tvRequestersName = itemView.findViewById(R.id.tvRequestersName)
            tvRequestedListingTitle = itemView.findViewById(R.id.tvRequestedListingTitle)
            tvRequestedListingAddress = itemView.findViewById(R.id.tvRequestedListingAddress)
            rbRequesterRating = itemView.findViewById(R.id.rbRequesterRating)
            buttonRejectRequest = itemView.findViewById(R.id.buttonRejectRequest)
            buttonAcceptRequest = itemView.findViewById(R.id.buttonAcceptRequest)
        }

        fun bind(request: ListingRequest) {
            // TODO: Put the information from the request into the views
            val query : ParseQuery<ParseUser> = ParseUser.getQuery()
            try {
                val user = query[request.objectId]

                Glide.with(itemView.context)
                    .load(user.getParseFile("profilePicture")?.url)
                    .into(ivRequesterProfilePhoto)

                tvRequestersName.text = user.getString("firstName") + " " + user.getString("lastName")

                // TODO: This might cause issues with casting, double check
                rbRequesterRating.rating = user.getDouble("rating").toFloat()

                tvRequestedListingTitle.text =
                    OwnerListingDetailFragment().listing?.getAddressStreet() + ", " +
                            OwnerListingDetailFragment().listing?.getAddressCity() + ", " +
                            OwnerListingDetailFragment().listing?.getAddressState() + " " +
                            OwnerListingDetailFragment().listing?.getAddressZip()
                tvRequestedListingAddress.text = OwnerListingDetailFragment().listing?.getTitle()

            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }
}