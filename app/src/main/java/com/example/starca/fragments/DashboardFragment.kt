package com.example.starca.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.starca.R
import com.example.starca.models.Listing
import com.parse.ParseQuery
import kotlinx.parcelize.Parcelize

@Parcelize
class DashboardFragment : Fragment(), Parcelable {


    private val DASHBOARD_KEY = "DASH_DATA"

    lateinit var rvListings: RecyclerView
    lateinit var adapter: ListingsAdapter

    var storageLocations = mutableListOf<StorageLocation>()


    var feedListings: MutableList<Listing> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val fragmentManager: FragmentManager = childFragmentManager

        val mapsFrag = MapsFragment()
        val bundle = Bundle()

        val a = DashboardData(storageLocations, this)

        bundle.putParcelable(DASHBOARD_KEY, a)
        mapsFrag.arguments = bundle
        fragmentManager.beginTransaction().replace(R.id.maps_container, mapsFrag)
            .commit()

        rvListings = view.findViewById(R.id.rvListings) as RecyclerView
        adapter = ListingsAdapter(requireContext(), feedListings)
        rvListings.adapter = adapter
        rvListings.layoutManager = LinearLayoutManager(requireContext())


        //i need to get listings.

        // add a 8 fake posts here
//        addListing("NJIT", "323 Dr Martin Luther King Jr Blvd, Newark, NJ 07102", "c", "d")
//        addListing("uhaul orange", "225 scotland rd", "c1", "d1")
//        addListing("cvs west orange", "265 prospect ave", "c2", "d2")
//        addListing("central park tower", "217 W 57th St, New York, NY 10019", "c3", "d3")
//        addListing("the MET", "1000 5th Ave", "c4", "d4")
        queryPosts()


        adapter.notifyDataSetChanged()

        return view
    }

//    private fun addListing(name : String, location : String, size : String, description : String) {
//        feedListings.add(Listing(name, location, size, description))
//        storageLocations.add(StorageLocation(name,location))
//    }

    @Parcelize
    class DashboardData(
        private val list: List<StorageLocation>,
        public val parent: DashboardFragment
    ) : Parcelable {
        fun storageLocations(): List<StorageLocation> {
            return list
        }
    }

    @Parcelize
    class StorageLocation(val name: String?, val address: String) : Parcelable

    fun selectMarker(index: Int) {
        Toast.makeText(context, "clicked hit!!!", Toast.LENGTH_SHORT).show()
    }

    private fun queryPosts() {
        val query: ParseQuery<Listing> = ParseQuery.getQuery(Listing::class.java)

//        query.include(Listing.KEY_USER)
//        query.addDescendingOrder("createdAt")

        query.limit = 10

        feedListings.clear()

        query.findInBackground { posts, e ->
            // don't convert this to lambda. i like to know what's being called.
            if (e != null) {
                Log.e(TAG, "Error fetching posts ")
            } else {
                if (posts != null) {
                    for (post in posts) {

                        fun getAddressName_Full(): String =
                            "${post.getAddressStreet()}, ${post.getAddressCity()}, ${post.getAddressState()} ${post.getAddressZip()}"

                        val addressName: String? = post.getTitle()

                        storageLocations.add(StorageLocation(addressName, getAddressName_Full()))
                    }
                    feedListings.addAll(posts)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        const val TAG = "DashboardFragment"
    }

}