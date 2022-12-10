package com.example.starca.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.starca.R
import com.example.starca.models.Listing
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.parse.ParseQuery
import com.parse.ParseUser

private const val LISTING_BUNDLE = "LISTING_BUNDLE"

class EditListingFragment : Fragment() {
    private var listing: Listing? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            listing = it.getParcelable(LISTING_BUNDLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setting variables for all TextInputLayout's and their corresponding TextInputEditText
        val titleTi = view.findViewById<TextInputEditText>(R.id.edit_listing_title_et)
        val titleLayoutTi = view.findViewById<TextInputLayout>(R.id.edit_listing_title_layout_et)

        val descriptionTi = view.findViewById<TextInputEditText>(R.id.edit_listing_description_et)
        val descriptionLayoutTi = view.findViewById<TextInputLayout>(R.id.edit_listing_description_layout_et)

        val streetTi = view.findViewById<TextInputEditText>(R.id.edit_listing_street_et)
        val streetLayoutTi = view.findViewById<TextInputLayout>(R.id.edit_listing_street_layout_et)

        val cityTi = view.findViewById<TextInputEditText>(R.id.edit_listing_city_et)
        val cityLayoutTi = view.findViewById<TextInputLayout>(R.id.edit_listing_city_layout_et)

        val stateTi = view.findViewById<TextInputEditText>(R.id.edit_listing_state_et)
        val stateLayoutTi = view.findViewById<TextInputLayout>(R.id.edit_listing_state_layout_et)

        val zipcodeTi = view.findViewById<TextInputEditText>(R.id.edit_listing_zipcode_et)
        val zipcodeLayoutTi = view.findViewById<TextInputLayout>(R.id.edit_listing_zipcode_layout_et)

        val editListingSubmitButton = view.findViewById<Button>(R.id.edit_listing_submit_button)
        val listingImageIv = view.findViewById<ImageView>(R.id.edit_listing_image_iv)

        // Set up enable-editing button for each edit text field
        titleLayoutTi.setEndIconOnClickListener { titleTi.isEnabled = true }
        descriptionLayoutTi.setEndIconOnClickListener { descriptionTi.isEnabled = true }
        streetLayoutTi.setEndIconOnClickListener { streetTi.isEnabled = true }
        cityLayoutTi.setEndIconOnClickListener { cityTi.isEnabled = true }
        stateLayoutTi.setEndIconOnClickListener { stateTi.isEnabled = true }
        zipcodeLayoutTi.setEndIconOnClickListener { zipcodeTi.isEnabled = true }

        // Set the edit text fields to the current value of the listing's fields
        titleTi.setText(listing?.getTitle())
        descriptionTi.setText(listing?.getDescription())
        streetTi.setText(listing?.getAddressStreet())
        cityTi.setText(listing?.getAddressCity())
        stateTi.setText(listing?.getAddressState())
        zipcodeTi.setText(listing?.getAddressZip())

        Glide.with(requireContext())
            .load(listing?.getImage()?.url)
            .placeholder(R.drawable.ic_profile)
            .transform(RoundedCorners(40))
            .into(listingImageIv)

        // TODO: Update new image stuff

        // Save changed fields on submit
        editListingSubmitButton.setOnClickListener {
            if (titleTi.isEnabled) {
                listing?.setTitle(titleTi.text.toString())
                titleTi.isEnabled = false
            }
            if (descriptionTi.isEnabled) {
                listing?.setDescription(descriptionTi.text.toString())
                descriptionTi.isEnabled = false
            }
            if (streetTi.isEnabled) {
                listing?.setAddressStreet(streetTi.text.toString())
                streetTi.isEnabled = false
            }
            if (cityTi.isEnabled) {
                listing?.setAddressCity(cityTi.text.toString())
                cityTi.isEnabled = false
            }
            if (stateTi.isEnabled) {
                listing?.setAddressState(stateTi.text.toString())
                stateTi.isEnabled = false
            }
            if (zipcodeTi.isEnabled) {
                listing?.setAddressZip(zipcodeTi.text.toString())
                zipcodeTi.isEnabled = false
            }
            saveChanges()
        }
    }

    fun saveChanges(){
        listing?.saveInBackground { e ->
            if (e == null) {
                // Successfully updated listing
                Log.i(DetailFragment.TAG, "Updated listing!")
                Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error updating listing", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}