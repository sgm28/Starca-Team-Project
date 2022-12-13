package com.example.starca.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.starca.models.ListingRequest
import com.example.starca.R
import com.example.starca.adapters.RequestsAdapter
import com.example.starca.models.Listing
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery

class OwnerListingDetailFragment : Fragment() {

    lateinit var rvListingRequests: RecyclerView
    lateinit var adapter: RequestsAdapter

    var allRequests: ArrayList<ListingRequest> = arrayListOf()

    var listing: Listing? = null

    lateinit var swipeContainer: SwipeRefreshLayout

    lateinit var cbVisibility: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_owner_listing_detail, container, false)

        arguments?.let {
            listing = it.getParcelable("LISTING_BUNDLE")
        }

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeContainer = view.findViewById<SwipeRefreshLayout>(R.id.swipeContainer)
        val titleTv = view.findViewById<TextView>(R.id.owner_listing_title_tv)
        val addressTv = view.findViewById<TextView>(R.id.owner_listing_address_tv)
        val editListingButton = view.findViewById<Button>(R.id.owner_listing_edit_button)

        addressTv.text =
            listing?.getAddressStreet() + ", " + listing?.getAddressCity() + ", " + listing?.getAddressState() + " " + listing?.getAddressZip()
        titleTv.text = listing?.getTitle()
        editListingButton.setOnClickListener {

            val bundle = Bundle()
            bundle.putParcelable(LISTING_BUNDLE, listing)
            val editListingFragment = EditListingFragment()
            editListingFragment.arguments = bundle

            val fragmentManager: FragmentManager = parentFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editListingFragment)
                .addToBackStack(null)
                .commit()
        }


        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        cbVisibility = view.findViewById(R.id.cbVisibility)
        cbVisibility.isChecked = getPostVisibilty(listing!!)
        cbVisibility.text = if (cbVisibility.isChecked) "visible" else "hidden"

        Log.i(TAG, "onViewCreated: ${getPostVisibilty(listing!!)}")
        cbVisibility.setOnClickListener { view ->
            if (view is CheckBox) {
                val checked = view.isChecked
                setPostVisibility(listing!!, checked)
                view.isClickable = false
                listing!!.saveInBackground { e ->
                    if (e != null) {
                        Log.e(TAG, "onViewCreated: error saving visibility")
                    } else {

                        view.text = if (checked) "visible" else "hidden"
                    }
                    view.isClickable = true
                }

            }
        }

        rvListingRequests = view.findViewById(R.id.rvListingRequests)

        adapter = RequestsAdapter(requireContext(), allRequests, listing!!)
        rvListingRequests.adapter = adapter
        rvListingRequests.layoutManager = LinearLayoutManager(requireContext())

        swipeContainer.setOnRefreshListener {
            queryRequests()
        }

        queryRequests()
    }

    fun queryRequests() {
        // Specify which class to query
        val query: ParseQuery<Listing> = ParseQuery.getQuery(Listing::class.java)

        Log.i(TAG, listing?.getJSONArray("listingRequests").toString())

        // Add the query constraints: Equal to the selected listing and has requests
        query.whereEqualTo("objectId", listing?.objectId)

        query.findInBackground(object : FindCallback<Listing> {
            override fun done(listings: MutableList<Listing>?, e: ParseException?) {
                if (e != null) {
                    Log.e(TAG, "Error fetching listing")
                    e.printStackTrace()
                } else {
                    if (listings != null) {
                        val queriedListing: Listing = listings[0]

                        var temp: MutableList<ListingRequest>? = mutableListOf()
                        if (listing?.getJSONArray("listingRequests") == null) {
                            temp = null
                        } else {
                            temp = listing!!.getJSONArray("listingRequests")
                                ?.let { ListingRequest.fromJsonArray(it, listing!!.objectId) }
                        }

                        val temp2: MutableList<ListingRequest> = mutableListOf()

                        if (temp != null) {
                            for (req in temp) {
                                if (req.status == FLAGS.DENIED.code || req.status == FLAGS.BOUGHT.code)
                                    temp2.add(req)
                            }
                        }

                        if (temp2.isNotEmpty()) {
                            temp?.removeAll(temp2)
                        }

                        if (temp.isNullOrEmpty()) {
                            adapter.clear()
                            view?.findViewById<TextView>(R.id.tvNoRequests)?.visibility =
                                View.VISIBLE
                        } else {
                            view?.findViewById<TextView>(R.id.tvNoRequests)?.visibility =
                                View.INVISIBLE
                            adapter.clear()
                            allRequests.addAll(temp)
                            adapter.notifyDataSetChanged()
                        }

                        swipeContainer.isRefreshing = false
                    }
                }
            }
        })
    }

    private fun getPostVisibilty(post: Listing): Boolean {
        return post.getBoolean(KEY_LISTING_VISIBILITY)
    }

    private fun setPostVisibility(post: Listing, setVisibile: Boolean) {
        Toast.makeText(context, "set vis: $setVisibile", Toast.LENGTH_SHORT).show()
        post.put(KEY_LISTING_VISIBILITY, setVisibile)
    }

    companion object {
        const val TAG = "OwnerListingDetailFragment"
        const val LISTING_BUNDLE = "LISTING_BUNDLE"
        const val KEY_LISTING_VISIBILITY = "listingVisibility"
    }
}