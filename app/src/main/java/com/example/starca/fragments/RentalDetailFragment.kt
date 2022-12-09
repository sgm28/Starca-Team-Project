package com.example.starca.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.starca.R
import com.example.starca.models.Listing
import com.example.starca.models.ListingRequest
import com.google.gson.Gson
import com.parse.ParseUser

private const val LISTING_BUNDLE = "LISTING_BUNDLE"

class RentalDetailFragment : Fragment() {

    private var listing: Listing? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            listing = it.getParcelable(LISTING_BUNDLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rental_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageIv = view.findViewById<ImageView>(R.id.rental_image_iv)
        val cityTv = view.findViewById<TextView>(R.id.rental_city_tv)
        val stateTv = view.findViewById<TextView>(R.id.rental_state_tv)
        val priceTv = view.findViewById<TextView>(R.id.rental_price_tv)
        val endRentalButton = view.findViewById<Button>(R.id.rental_end_button)

        Glide.with(requireContext()).load(listing?.getParseFile("PictureOfListing")?.url)
            .into(imageIv)

        cityTv.text = listing?.getAddressCity() + ", "
        stateTv.text = listing?.getAddressState()
        val listingPrice = listing?.getPrice()
        priceTv.text = String.format("$%.2f", listingPrice)

        endRentalButton.setOnClickListener {

            removeListingFromUser()

            tryRemoveListingRequest()

            // Return to profile page after ending the listing agreement
            val profileFragment = ProfileFragment()
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, profileFragment)
                .commit()
        }
    }

    private fun tryRemoveListingRequest() {
        val requests_arrayJSON = listing?.getJSONArray("listingRequests")

        val requests = requests_arrayJSON?.let { jsonArray ->
            listing?.let { li ->
                ListingRequest.fromJsonArray(
                    jsonArray,
                    li.objectId
                )
            }
        }
        if (requests != null) {
            for (request in requests) {
                if (request.objectId == ParseUser.getCurrentUser().objectId) {
                    removeListingRequest(requests, request)
                }
            }
        }
    }

    private fun removeListingFromUser() {
        val user = ParseUser.getCurrentUser()
        val rentedListingIds: ArrayList<String>? =
            ParseUser.getCurrentUser().getList<String>("rentedListings") as ArrayList<String>?

        // Remove listing from user's listings' ID array
        listing!!.objectId.let { rentedListingIds?.remove(it) }

        if (rentedListingIds != null) {
            user.put("rentedListings", rentedListingIds)

            // Save changes
            user.saveInBackground { e ->
                if (e == null) {
                    // User successfully rented the listing
                    Log.i(DetailFragment.TAG, "Ended listing agreement!")
                } else {
                    Toast.makeText(requireContext(), "Error buying listing", Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            }
        }
    }

    fun removeListingRequest(
        requestArray: MutableList<ListingRequest>,
        request: ListingRequest
    ): View.OnClickListener {
        return View.OnClickListener { _ ->
            //return to original request view

            // you are supposed to remove the listingRequest here. not the listing.
            requestArray.remove(request)

            val gson = Gson()

            val stArr = ArrayList<String>()
            for (r in requestArray) {
                stArr.add(gson.toJson(r))
            }

            listing?.put("listingRequests", stArr)

            listing?.saveInBackground { e ->
                if (e == null) {
                    Toast.makeText(context, "Test: Request Removed", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "removeListingRequest: $e")
                }
            }
        }
    }

    companion object {
        const val TAG = "RentalDetailFragment"
    }
}