package com.example.starca.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.starca.R
import com.example.starca.SettingsActivity
import com.example.starca.adapters.ListingsGridAdapter
import com.example.starca.models.Listing
import com.parse.*
import org.w3c.dom.Text

/**TODO:
 *      Remove the top bar because it's not very useful and makes the app look less "air-y" or less flowy. Dunno how to describe
 *      Work on other stuff (and continue styling the profile. Also, add a tab to see currently renting places)
 *      Also, add a boolean (to the listing) so we can show if the listing is being rented or not
 *      Add price to the listing
 */

class ProfileFragment : Fragment() {

    lateinit var listingsGridView: GridView
    private lateinit var adapter: ListingsGridAdapter
    val listingsArrayList = ArrayList<Listing>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = ParseUser.getCurrentUser();
        var fullNameTv: TextView = view.findViewById(R.id.fullname_tv)
        var bioTv: TextView = view.findViewById(R.id.bio_tv)
        var ratingBar: RatingBar = view.findViewById(R.id.user_rating_rb)
        var profileIv: ImageView = view.findViewById((R.id.profile_iv))

        // Set up grid view
        listingsGridView = view.findViewById(R.id.personal_listings_gv)
        adapter = ListingsGridAdapter(requireContext(), listingsArrayList)
        listingsGridView.adapter = adapter

        // Set user profile details
        val fullName: String = user.getString("firstName") + " " + user.getString("lastName")
        fullNameTv.text = fullName
        bioTv.text = user.getString("bio")
        ratingBar.rating = user.getDouble("rating").toFloat()

        Glide.with(requireContext())
            .load(user.getParseFile("profilePicture")?.url)
            .placeholder(R.drawable.ic_profile)
            .circleCrop()
            .into(profileIv)

        view.findViewById<ImageButton>(R.id.settingsButton).setOnClickListener {
            goToSettingsActivity()
        }

        queryListings()
    }

    private fun goToSettingsActivity() {
        val intent = Intent(activity, SettingsActivity::class.java)
        startActivity(intent)
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