package com.example.starca.fragments

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
            //TODO: Remove listing request by this user for this listing

            // Return to profile page after ending the listing agreement
            val profileFragment = ProfileFragment()
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, profileFragment)
                .commit()
        }
    }

    private fun removeListingFromUser(){
        val user = ParseUser.getCurrentUser()
        val rentedListingIds: ArrayList<String>? = ParseUser.getCurrentUser().getList<String>("rentedListings") as ArrayList<String>?

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
                    Toast.makeText(requireContext(), "Error buying listing", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }
}