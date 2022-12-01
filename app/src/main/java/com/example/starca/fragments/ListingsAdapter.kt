package com.example.starca.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.starca.DetailFragment
import com.example.starca.R
import com.example.starca.models.Listing


const val LISTING_BUNDLE = "LISTING_BUNDLE"

class ListingsAdapter(
    private val context: Context,
    private val listings: List<Listing>,
) :
    RecyclerView.Adapter<ListingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = listings[position]
        holder.bind(post)
    }

    override fun getItemCount() = listings.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val ivPoster: ImageView
        private val tvName: TextView
        private val tvLocation: TextView
        private val tvSize: TextView
        private val tvDesc: TextView

        init {
            ivPoster = itemView.findViewById<ImageView>(R.id.ivPreview)
            tvName = itemView.findViewById<TextView>(R.id.tvName)
            tvLocation = itemView.findViewById<TextView>(R.id.tvLocation)
            tvSize = itemView.findViewById<TextView>(R.id.tvSize)
            tvDesc = itemView.findViewById<TextView>(R.id.tvDescription)

            itemView.setOnClickListener(this)
        }

        fun bind(post: Listing) {

            fun getAddressName_Simp(): String =
                "${post.getAddressCity()}, ${post.getAddressState()} ${post.getAddressZip()}"

            tvName.text = post.getTitle()
            tvLocation.text = getAddressName_Simp()
            tvSize.text = post.getDimensions()
            tvDesc.text = post.getDescription()

            Glide.with(itemView.context).load(post.getImage()?.url).placeholder(R.drawable.starca_logo_icon).transform(RoundedCorners(20)).into(ivPoster)

            ViewCompat.setTransitionName(ivPoster, "transition_dashboard_image$adapterPosition")
            ViewCompat.setTransitionName(tvDesc, "transition_dashboard_description$adapterPosition")
            ViewCompat.setTransitionName(tvName, "transition_dashboard_title$adapterPosition")
        }

        override fun onClick(v: View) {

            val listing = listings[adapterPosition]

            // Create bundle containing selected listing and add to detailFragment object
            val bundle = Bundle()
            bundle.putParcelable(LISTING_BUNDLE, listing)
//            bundle.putString("transitionTitle", "transition_dashboard_title$adapterPosition")
            val detailFragment = DetailFragment()
            detailFragment.arguments = bundle

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(tvName, "transition_dashboard_title")
                .addSharedElement(ivPoster, "transition_dashboard_image")
                .addSharedElement(tvDesc, "transition_dashboard_description")
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()

            //TODO: Add shared element transition
        }
    }
}
