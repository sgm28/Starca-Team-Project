package com.example.starca.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.starca.R
import com.example.starca.SettingsActivity
import com.example.starca.adapters.ProfileViewPager2Adapter
import com.google.android.material.tabs.TabLayout
import com.parse.*

class ProfileFragment : Fragment() {

    lateinit var viewPager2Adapter: ProfileViewPager2Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        showCustomUI()
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

    override fun onStop() {
        super.onStop()
        removeCustomUI()
    }

    private fun goToSettingsActivity() {
        val intent = Intent(activity, SettingsActivity::class.java)
        startActivity(intent)
    }

    // App take becomes "edge-to-edge", takes up the whole screen
    private fun showCustomUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(true)
        } else {
            // Keeping deprecated "setSystemUiVisibility" for older android versions
            val decorView: View = requireActivity().window.decorView
            decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
        }
    }

    // Remove edge-to-edge behavior
    private fun removeCustomUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
        } else {
            val decorView: View = requireActivity().window.decorView
            decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            )
        }
    }
}