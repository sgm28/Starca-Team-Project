package com.example.starca.fragments

import android.os.Bundle
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

class ListingsChildFragment : Fragment() {

    lateinit var listingsGridView: GridView
    private lateinit var adapter: ListingsGridAdapter
    val listingsArrayList = ArrayList<Listing>()

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
        listingsGridView = view.findViewById(R.id.personal_listings_gv)
        adapter = ListingsGridAdapter(requireContext(), listingsArrayList)
        listingsGridView.adapter = adapter

        queryListings()
    }

    fun queryListings() {
        listingsArrayList.clear()
        val query: ParseQuery<Listing> = ParseQuery(Listing::class.java)

        // Asking parse to also include the user that posted the Listing (since user is a pointer in the Listing table)
        query.include(Listing.KEY_USER)
        // Returns only current user's listings
        query.whereEqualTo(Listing.KEY_USER, ParseUser.getCurrentUser())
        query.addDescendingOrder("createdAt")
        query.findInBackground { listings, e ->
            if (e != null) {
                Toast.makeText(requireContext(), "Couldn't fetch listings", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (listings != null) {
                    listingsArrayList.addAll(listings)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}