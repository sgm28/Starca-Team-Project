package com.example.starca.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import com.example.starca.R
import com.example.starca.adapters.ListingsGridAdapter
import com.example.starca.models.Listing
import com.parse.ParseQuery
import com.parse.ParseUser


class RentedChildFragment : Fragment() {

    lateinit var rentedListingsGridView: GridView
    private lateinit var rentedAdapter: ListingsGridAdapter
    private val rentedListingsArrayList = ArrayList<Listing>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listings_child, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up grid view
        rentedListingsGridView = view.findViewById(R.id.personal_listings_gv)
        rentedAdapter = ListingsGridAdapter(requireContext(), rentedListingsArrayList, false)
        rentedListingsGridView.adapter = rentedAdapter

        // Get the user's array of rented listing IDs
        val rentedListingIds: List<String>? = ParseUser.getCurrentUser().getList<String>("rentedListings")
        if (!rentedListingIds.isNullOrEmpty()) {
            queryRentedListings(rentedListingIds)
        }
    }

    fun queryRentedListings(rentedListingIds: List<String>?) {
        rentedListingsArrayList.clear()
        val query: ParseQuery<Listing> = ParseQuery(Listing::class.java)

        // Asking parse to also include the user that posted the Listing (since user is a pointer in the Listing table)
        query.include(Listing.KEY_USER)
        // Querying listings that are equal to the ID's in the user's rented list
        query.whereContainedIn(Listing.KEY_ID, rentedListingIds)
        query.addDescendingOrder("createdAt")
        query.findInBackground { listings, e ->
            if (e != null) {
                Toast.makeText(requireContext(), "Couldn't fetch listings", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (listings != null) {
                    for (listing in listings) {
                        listing.getTitle()?.let { Log.d("RentedArrayList:", it) }
                    }
                    rentedListingsArrayList.addAll(listings)
                    Log.d("RentedArrayList:", rentedListingsArrayList.toString())
                    rentedAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}