package com.example.starca.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.starca.fragments.FLAGS
import com.example.starca.R
import com.example.starca.models.ListingRequest
import com.example.starca.models.Listing
import com.google.gson.Gson
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser
import de.hdodenhof.circleimageview.CircleImageView

class RequestsAdapter(val context: Context, val requests: ArrayList<ListingRequest>, val listing: Listing) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestsAdapter.ViewHolder, position: Int) {
        val request = requests[position]

        holder.bind(request, listing)

        holder.buttonRejectRequest.setOnClickListener {
            updateRequestStatusCode(listing, requests, position, FLAGS.DENIED.code)

            holder.buttonRejectRequest.visibility = View.INVISIBLE
            holder.buttonAcceptRequest.visibility = View.INVISIBLE
            holder.tvRequestStatus.visibility = View.VISIBLE
            holder.tvRequestStatus.text = "Rejected"
        }
        holder.buttonAcceptRequest.setOnClickListener {
            updateRequestStatusCode(listing, requests, position, FLAGS.APPROVED.code)
            holder.buttonRejectRequest.visibility = View.INVISIBLE
            holder.buttonAcceptRequest.visibility = View.INVISIBLE
            holder.tvRequestStatus.visibility = View.VISIBLE
            holder.tvRequestStatus.text = "Awaiting Payment"
        }
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    fun clear() {
        requests.clear()
        notifyDataSetChanged()
    }

    fun updateRequestStatusCode(listing: Listing, requests: MutableList<ListingRequest>, position: Int, statusCode: Int) {

        val userID = requests[position].objectId
        val listingID = listing.objectId

        val updatedRequest = ListingRequest(
            userID,
            statusCode,
            listingID
        )

        requests[position] = updatedRequest

        val gson = Gson()
        val stArr = ArrayList<String>()
        for (request in requests) {
            stArr.add(gson.toJson(request))
        }

        listing.put("listingRequests", stArr)

        listing.saveInBackground { e ->
            if (e == null) {
                when (statusCode) {
                    FLAGS.DENIED.code -> {
                        Toast.makeText(
                            this.context,
                            "Request has been rejected.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    FLAGS.APPROVED.code -> {
                        Toast.makeText(
                            this.context,
                            "Request has been accepted.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this.context, "Error sending decision", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivRequesterProfilePhoto : CircleImageView
        val tvRequestersName : TextView
        val tvRequestStatus : TextView
        val rbRequesterRating : RatingBar
        val buttonRejectRequest : ImageButton
        val buttonAcceptRequest : ImageButton

        init {
            ivRequesterProfilePhoto = itemView.findViewById(R.id.ivRequesterProfilePhoto)
            tvRequestersName = itemView.findViewById(R.id.tvRequestersName)
            rbRequesterRating = itemView.findViewById(R.id.rbRequesterRating)
            buttonRejectRequest = itemView.findViewById(R.id.buttonRejectRequest)
            buttonAcceptRequest = itemView.findViewById(R.id.buttonAcceptRequest)
            tvRequestStatus = itemView.findViewById(R.id.tvRequestStatus)
        }

        fun bind(request: ListingRequest, listing : Listing) {
            val query : ParseQuery<ParseUser> = ParseUser.getQuery()
            try {
                val user = query[request.objectId]

                when (request.status) {
                    FLAGS.REQUESTED.code -> {
                        tvRequestStatus.visibility = View.INVISIBLE
                        buttonAcceptRequest.visibility = View.VISIBLE
                        buttonRejectRequest.visibility = View.VISIBLE
                    }
                    FLAGS.APPROVED.code -> {
                        tvRequestStatus.visibility = View.VISIBLE
                        tvRequestStatus.text = "Awaiting Payment"
                        buttonAcceptRequest.visibility = View.INVISIBLE
                        buttonRejectRequest.visibility = View.INVISIBLE
                    }
                }

                Glide.with(itemView.context)
                    .load(user.getParseFile("profilePicture")?.url)
                    .into(ivRequesterProfilePhoto)

                tvRequestersName.text =
                    user.getString("firstName") + " " + user.getString("lastName")

                rbRequesterRating.rating = user.getDouble("rating").toFloat()

            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }
}