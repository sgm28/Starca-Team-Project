package com.example.starca

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.example.starca.models.Listing
import com.parse.ParseObject

private const val LISTING_BUNDLE = "LISTING_BUNDLE"
//private const val TRANSITION_TITLE_BUNDLE = "transitionTitle"
//private const val TRANSITION_IMAGE_BUNDLE = "transitionImage"
//private const val TRANSITION_CITY_BUNDLE = "transitionCity"
//private const val TRANSITION_STATE_BUNDLE = "transitionState"

class DetailFragment : Fragment() {
    private var listing: ParseObject? = null
    private var transitionTitle: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_image)
        arguments?.let {
            listing = it.getParcelable(LISTING_BUNDLE)
//            transitionTitle = it.getString(TRANSITION_TITLE_BUNDLE)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }
/*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
    */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleTv = view.findViewById<TextView>(R.id.detail_title_tv)
        val cityTv = view.findViewById<TextView>(R.id.detail_city_tv)
        val stateTv = view.findViewById<TextView>(R.id.detail_state_tv)
        val ratingRb = view.findViewById<RatingBar>(R.id.detail_rating_rb)
        val descriptionTv = view.findViewById<TextView>(R.id.detail_description_tv)
        val imageIv = view.findViewById<ImageView>(R.id.detail_image_iv)
        ViewCompat.setTransitionName(imageIv, "transition_dashboard_image")
        ViewCompat.setTransitionName(titleTv, "transition_dashboard_title")
        ViewCompat.setTransitionName(descriptionTv, "transition_dashboard_description")

        titleTv.text = listing?.getString("title")
        cityTv.text = listing?.getString("addressCity") + ", "
        stateTv.text = listing?.getString("addressState")
        ratingRb.rating = listing?.getDouble("listingRating")!!.toFloat()
        descriptionTv.text = listing?.getString("description")

        Glide.with(requireContext()).load(listing?.getParseFile("PictureOfListing")?.url).into(imageIv)

        //TODO: put startPostponedEnterTransition() in glide callback
        startPostponedEnterTransition()
    }
}