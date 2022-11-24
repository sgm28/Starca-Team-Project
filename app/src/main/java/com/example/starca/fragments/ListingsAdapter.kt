package com.example.starca.fragments


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.starca.R
import com.example.starca.models.Listing
import java.text.SimpleDateFormat


// just leave this here until i finish this page.
//const val MOVIE_EXTRA = "MOVIE_EXTRA"

class ListingsAdapter(
    private val context: Context,
    private val listings: List<Listing>,// change this to a Listing model item
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

            // no image here so far.
            fun getAddressName_Simp(): String =
                "${post.getAddressCity()}, ${post.getAddressState()} ${post.getAddressZip()}"


            tvName.text = post.getTitle()
            tvLocation.text = getAddressName_Simp()
            tvSize.text = post.getDimensions()
            tvDesc.text = post.getDescription()

            // TODO: add image of storage to listings. placeholder if null.
            //            Glide.with(itemView.context).load(post.getImage()?.url).into(ivPoster)

        }


        override fun onClick(v: View) {

            Toast.makeText(context, "clicked $adapterPosition", Toast.LENGTH_SHORT).show()

            // TODO:
            //  add details page when i get an image to show

            // below: working code from flixster stretch story. animates into details view. don't delete.

//            val movie = movies[adapterPosition]
////            Toast.makeText(context, movie.title, Toast.LENGTH_SHORT).show()
//            val intent = Intent(context, DetailActivity::class.java)
//
//            val p1: Pair<View, String> = Pair.create(ivPoster as View, "profile")
//            val p2: Pair<View, String> = Pair.create(tvOverview as View, "overview")
//            val p3: Pair<View, String> = Pair.create(tvTitle as View, "title")
//
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                activity,
//                p1,p2,p3
//            )
//
//            intent.putExtra(MOVIE_EXTRA, movie)
//            context.startActivity(intent, options.toBundle())

        }
    }
}
