package com.example.starca.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.starca.DetailFragment
import com.example.starca.ListingRequest
import com.example.starca.R
import com.example.starca.adapters.RequestsAdapter
import com.example.starca.models.Listing
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import org.json.JSONArray

class OwnerListingDetailFragment : Fragment() {



    lateinit var rvListingRequests : RecyclerView
    lateinit var adapter: RequestsAdapter

    var allRequests: ArrayList<ListingRequest> = arrayListOf()

    open var listing : Listing? = null

    lateinit var swipeContainer : SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_owner_listing_detail, container, false)

        arguments?.let {
            listing = it.getParcelable("LISTING_BUNDLE")
        }

        rvListingRequests = view.findViewById(R.id.rvListingRequests)
        swipeContainer = view.findViewById(R.id.swipeContainer)

        adapter = RequestsAdapter(requireContext(), allRequests, listing!!)
        rvListingRequests.adapter = adapter
        rvListingRequests.layoutManager = LinearLayoutManager(requireContext())

        swipeContainer.setOnRefreshListener {
            queryRequests()
        }

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

        queryRequests()
    }

    fun queryRequests() {
        // Specify which class to query
        val query : ParseQuery<Listing> = ParseQuery.getQuery(Listing::class.java)

        Log.i(TAG, listing?.getJSONArray("listingRequests").toString())

        // Add the query constraints: Equal to the selected listing and has requests
        query.whereEqualTo("objectId", listing?.objectId)
        query.whereNotEqualTo("listingRequests", null)

        query.findInBackground(object: FindCallback<Listing> {
            override fun done(listings: MutableList<Listing>?, e: ParseException?) {
                if (e != null) {
                    Log.e(TAG, "Error fetching listing")
                    e.printStackTrace()
                } else {
                    if (listings != null) {
                        val queriedListing : Listing = listings[0]
                        val temp = listing!!.getJSONArray("listingRequests")
                            ?.let { ListingRequest.fromJsonArray(it, listing!!.objectId) }

                        adapter.clear()

                        if (temp.isNullOrEmpty()) {
                            Toast.makeText(requireContext(), "No Requests for the selected listing", Toast.LENGTH_SHORT).show()
                        } else {
                            allRequests.addAll(temp)
                            adapter.notifyDataSetChanged()
                        }

                        swipeContainer.isRefreshing = false
                    }
                }
            }
        })
    }
    companion object {
        const val TAG = "OwnerListingDetailLFragment"
    }
}