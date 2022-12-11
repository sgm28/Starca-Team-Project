package com.example.starca.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.starca.R
import com.example.starca.models.Listing
import com.example.starca.models.ListingRating
import com.example.starca.models.ListingRequest
import com.google.gson.Gson
import com.parse.*


private const val LISTING_BUNDLE = "LISTING_BUNDLE"

class RentalDetailFragment : Fragment() {

    private var listing: Listing? = null
    private var myListingRating: ListingRating? = null

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
        val ratingRb = view.findViewById<RatingBar>(R.id.rental_rb)

        Glide.with(requireContext()).load(listing?.getParseFile("PictureOfListing")?.url)
            .into(imageIv)

        cityTv.text = listing?.getAddressCity() + ", "
        stateTv.text = listing?.getAddressState()
        val listingPrice = listing?.getPrice()
        priceTv.text = String.format("$%.2f", listingPrice)

        // If user has rated this listing, fetch that ListingRating's rating and load it into the rating bar, ratingRb
        getListingRating(ratingRb)

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

        // Set up rating bar listener
        ratingRb.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            // If the user updated the ratingBar, set user's rating for this listing
            if (fromUser) {
                rateListing(rating)
            }
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

    private fun calculateAverageRating() {
        val parameters: HashMap<String, String?> = HashMap()
        parameters["listing"] = listing?.objectId

        // This calls the function in Parse Cloud Code that calculates the average rating of a listing based on all it's rating's
        ParseCloud.callFunctionInBackground("averageRating", parameters, object: FunctionCallback<String?> {
            override fun done(avgRating: String?, e: ParseException?) {
                if (e == null) {
                    //Update listing's rating with the new average rating
                    avgRating?.toFloat()?.let { listing?.setRating(it) }
                    listing?.saveInBackground()
                } else {
                    Log.d("AverageRatingError", e.printStackTrace().toString())
                }
            }
        })
    }

    private fun getListingRating(rb: RatingBar) {

        val query: ParseQuery<ListingRating> = ParseQuery.getQuery(ListingRating::class.java)

        query.include(ListingRating.KEY_USER)

        query.whereEqualTo(ListingRating.KEY_LISTING_ID, listing?.objectId)
        query.whereEqualTo(ListingRating.KEY_USER, ParseUser.getCurrentUser())
        query.findInBackground { listingRating, e ->
            if (e != null) {
                Log.e(ConversationsFragment.TAG, "Error fetching rating ${e.message}")
            } else {
                if (listingRating.isNotEmpty()) {
                    myListingRating = listingRating[0]
                    setRatingBarRating(rb)
                }
            }
        }
    }

    private fun rateListing(rating: Float) {

        if (myListingRating != null) {
            // Update rating for the already existing ListingRating for this user and listing
            myListingRating?.setRating(rating)
        } else {
            // If no rating found, add a new rating for this listing
            Log.d("ListingRating", "Create new rating with value $rating")
            myListingRating = ListingRating()
            myListingRating?.setRating(rating)
            myListingRating?.setUser(ParseUser.getCurrentUser())
            listing?.objectId?.let { myListingRating?.setListingId(it) }
        }

        myListingRating?.saveInBackground{ e ->
            if (e == null) {
                // Successfully updated rating
                Log.i("ListingRating", "Updated or created use rating for listing")
                // After saving the new/updated rating, calculate this listing's new average
                // and save it to the listing's rating field
                calculateAverageRating()
            } else {
                e.printStackTrace()
            }
        }
    }

    private fun setRatingBarRating(rb: RatingBar) {
        if (myListingRating != null) {
            rb.rating = myListingRating?.getRating()!!
        } else {
            Log.d("RatingBar", "Did not set rating. Listing Rating was null")
        }
    }

    companion object {
        const val TAG = "RentalDetailFragment"
    }
}