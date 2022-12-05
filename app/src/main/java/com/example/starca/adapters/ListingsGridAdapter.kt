package com.example.starca.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.starca.R
import com.example.starca.fragments.OwnerListingDetailFragment
import com.example.starca.models.Listing

internal class ListingsGridAdapter (
    private val context: Context,
    private val listings: ArrayList<Listing>
        ) : BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var gridImage: ImageView
    private lateinit var gridTitle: TextView
    private lateinit var gridRating: RatingBar
    private lateinit var gridCity: TextView
    private lateinit var gridState: TextView


    override fun getCount(): Int {
        return listings.size
    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var listingGridView = view

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context)
        }

        if (listingGridView == null) {
            listingGridView = layoutInflater!!.inflate(R.layout.item_listing_gridview, null)
        }

        gridImage = listingGridView!!.findViewById<ImageView>(R.id.grid_image_iv)
        gridTitle = listingGridView!!.findViewById<TextView>(R.id.grid_title_tv)
        gridRating = listingGridView!!.findViewById<RatingBar>(R.id.grid_rating_rb)
        gridCity = listingGridView!!.findViewById<TextView>(R.id.grid_city_tv)
        gridState = listingGridView!!.findViewById<TextView>(R.id.grid_state_tv)

        val listing = listings[position]
        Glide.with(listingGridView.context).load(listing.getImage()?.url).transform(RoundedCorners(40)).into(gridImage)
        gridTitle.text = listing.getTitle()
        gridRating.rating = listing.getRating()!!
        gridCity.text = listing.getAddressCity() + ", "
        gridState.text = listing.getAddressState()


        // TODO: Fix this to get requests and display them.
        listingGridView.setOnClickListener {
            onClick(listingGridView, listings[position])
        }

        return listingGridView

    }

//    fun onClick(v: View, listings: ArrayList<Listing>, position: Int) {
private fun onClick(v: View, listing: Listing) {
//        val listing = listings[position]

        val bundle = Bundle()
        bundle.putParcelable(LISTING_BUNDLE, listing)
        val ownerListingDetailFragment = OwnerListingDetailFragment()
        ownerListingDetailFragment.arguments = bundle

        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ownerListingDetailFragment)
            .addToBackStack(null)
            .commit()
    }
    companion object {
        val LISTING_BUNDLE = "LISTING_BUNDLE"
    }
}