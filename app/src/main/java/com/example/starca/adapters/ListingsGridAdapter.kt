package com.example.starca.adapters

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.starca.R
import com.example.starca.models.Listing

internal class ListingsGridAdapter (
    private val context: Context,
    private val listings: ArrayList<Listing>
        ) : BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var gridImage: ImageView
    private lateinit var gridTitle: TextView
    private lateinit var gridRating: RatingBar


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

        val listing = listings[position]
        Glide.with(listingGridView.context).load(listing.getImage()?.url).into(gridImage)
        gridTitle.text = listing.getTitle()
        gridRating.rating = listing.getRating()!!

        return listingGridView
    }

}