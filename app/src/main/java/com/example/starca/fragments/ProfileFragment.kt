package com.example.starca.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.starca.R
import com.example.starca.SettingsActivity
import com.parse.Parse
import com.parse.ParseUser
import org.w3c.dom.Text

class ProfileFragment : Fragment() {

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

        view.findViewById<ImageButton>(R.id.settingsButton).setOnClickListener {
            goToSettingsActivity()
        }

        // Set user profile details
        val fullName: String = user.getString("firstName") + " " + user.getString("lastName")
        fullNameTv.text = fullName
        bioTv.text = user.getString("bio")
        ratingBar.rating = user.getDouble("rating").toFloat()
        Log.i("Rating Bar", user.getDouble("rating").toString())

        Glide.with(requireContext())
            .load(user.getParseFile("profilePicture")?.url)
            .placeholder(R.drawable.ic_profile)
            .circleCrop()
            .into(profileIv)
    }

    private fun goToSettingsActivity() {
        val intent = Intent(activity, SettingsActivity::class.java)
        startActivity(intent)
    }
}