package com.example.starca.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.starca.R
import com.example.starca.SettingsActivity
import com.example.starca.adapters.ListingsGridAdapter
import com.example.starca.adapters.ProfileViewPager2Adapter
import com.example.starca.models.Listing
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.parse.*
import org.w3c.dom.Text

/**TODO:
 *      Remove the top bar because it's not very useful and makes the app look less "air-y" or less flowy. Dunno how to describe
 *      Work on other stuff (and continue styling the profile. Also, add a tab to see currently renting places)
 *      Also, add a boolean (to the listing) so we can show if the listing is being rented or not
 *      Add price to the listing
 */

class ProfileFragment : Fragment() {


    lateinit var viewPager2Adapter: ProfileViewPager2Adapter

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
        val tabLayout: TabLayout = view.findViewById(R.id.profile_tl)
        val viewPager2: ViewPager2 = view.findViewById(R.id.profile_vp2)

        viewPager2Adapter = ProfileViewPager2Adapter(this)
        viewPager2.adapter = viewPager2Adapter

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

        // Set up tab layout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager2.currentItem = tab!!.position
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })

        // Allows selected-tab update on page swipe
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.getTabAt(position)?.select()
            }
        })
    }

    private fun goToSettingsActivity() {
        val intent = Intent(activity, SettingsActivity::class.java)
        startActivity(intent)
    }
}