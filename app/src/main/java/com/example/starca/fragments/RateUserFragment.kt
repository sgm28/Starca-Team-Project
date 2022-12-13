package com.example.starca.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.starca.R
import com.example.starca.models.UserRating
import com.google.android.material.snackbar.Snackbar
import com.parse.*

private const val USER_TO_RATE = "USER_TO_RATE"

class RateUserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var userToRate: ParseUser? = null
    private var userRating: UserRating? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userToRate = it.getParcelable(USER_TO_RATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rate_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fullNameTv = view.findViewById<TextView>(R.id.rate_user_fullname_tv)
        val rateUserRb = view.findViewById<RatingBar>(R.id.rate_user_rb)
        val rateUserIv = view.findViewById<ImageView>(R.id.rate_user_profile_iv)
        val rateText = "How do you feel about " + userToRate?.getString("firstName") + " " + userToRate?.getString("lastName") + "?"

        // If user has rated this owner, fetch that rating and load it into the rating bar, rateUserRb
        getUserRating(rateUserRb)
        fullNameTv.text = rateText

        Glide.with(requireContext())
            .load(userToRate?.getParseFile("profilePicture")?.url)
            .circleCrop()
            .into(rateUserIv)

        // Set up rating bar listener
        rateUserRb.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            // If the user updated the ratingBar, set user's rating for the listing's owner
            if (fromUser) {
                rateUser(rating)
            }
        }

    }

    private fun rateUser(rating: Float) {

        if (userRating != null) {
            // Update rating for the already existing UserRating for this renter and owner
            userRating?.setRating(rating)
        } else {
            // If no rating found, add a new UserRating for this owner
            Log.d("UserRating", "Create new rating with value $rating")
            userRating = UserRating()
            userRating?.setRating(rating)
            userRating?.setUser(ParseUser.getCurrentUser())
            userToRate?.objectId?.let { userRating?.setUserToRateId(it) }
        }

        userRating?.saveInBackground{ e ->
            if (e == null) {
                // After saving the new/updated rating, calculate this user's new average rating
                // and save it to the user's rating field
                calculateAverageRatingForUser()
            } else {
                e.printStackTrace()
            }
        }
    }

    private fun calculateAverageRatingForUser() {
        val parameters: HashMap<String, String?> = HashMap()
        parameters["userToRateId"] = userToRate?.objectId

        // This calls the function in Parse Cloud Code that calculates the average rating of a user based on all their rating's
        ParseCloud.callFunctionInBackground("averageRatingForUser", parameters, object:
            FunctionCallback<String?> {
            override fun done(avgRating: String?, e: ParseException?) {
                if (e == null) {
                    if (avgRating != null) {
                        // Update the user's average rating in Parse Cloud Code
                        updateUserToRate(avgRating)
                    }
                } else {
                    Log.d("AverageRatingError", e.printStackTrace().toString())
                }
            }
        })
    }

    private fun getUserRating(rb: RatingBar) {

        val query: ParseQuery<UserRating> = ParseQuery.getQuery(UserRating::class.java)

        query.include(UserRating.KEY_USER)

        query.whereEqualTo(UserRating.KEY_USER_TO_RATE_ID, userToRate?.objectId)
        query.whereEqualTo(UserRating.KEY_USER, ParseUser.getCurrentUser())
        query.findInBackground { queriedRating, e ->
            if (e != null) {
                Log.e(ConversationsFragment.TAG, "Error fetching rating ${e.message}")
            } else {
                if (queriedRating.isNotEmpty()) {
                    userRating = queriedRating[0]
                    setRatingBarRating(rb)
                }
            }
        }
    }

    private fun setRatingBarRating(rb: RatingBar) {
        if (userRating != null) {
            rb.rating = userRating?.getRating()!!
        } else {
            Log.d("RatingBar", "Did not set rating. UserRating was null")
        }
    }

    private fun updateUserToRate(avgRating: String) {
        val parameters: HashMap<String, String?> = HashMap()
        parameters["userToRateId"] = userToRate?.objectId
        parameters["newAvgRating"] = avgRating

        ParseCloud.callFunctionInBackground("editUserRating", parameters, object:
            FunctionCallback<String?> {
            override fun done(res: String?, e: ParseException?) {
                if (e == null) {
                    if (res != null) {
                        // Update the user's average rating in Parse Cloud Code
                        Log.d("RatingEditUser", res)
                        Snackbar.make(view!!, "Saved rating!", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
                        backToRentalDetail()
                    }
                } else {
                    Log.d("AverageRatingError", e.printStackTrace().toString())
                }
            }
        })
    }

    private fun backToRentalDetail() {
        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
        fragmentManager.popBackStack()
    }
}