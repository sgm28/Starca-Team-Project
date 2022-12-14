package com.example.starca.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.starca.FileUtils
import com.example.starca.R
import com.example.starca.models.Listing
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.parse.ParseFile
import com.parse.ParseQuery
import com.parse.ParseUser
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException

private const val LISTING_BUNDLE = "LISTING_BUNDLE"

class EditListingFragment : Fragment() {
    private var listing: Listing? = null
    val SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1046
    var photoFile: File? = null


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

        val dimensionsTi = view.findViewById<TextInputEditText>(R.id.edit_listing_dimensions_et)
        val dimensionsLayoutTi = view.findViewById<TextInputLayout>(R.id.edit_listing_dimensions_layout_et)

        val chooseNewPhotoButton = view.findViewById<ImageButton>(R.id.editListingPicture)
        val listingImageIv = view.findViewById<ImageView>(R.id.edit_listing_image_iv)

        val editListingSubmitButton = view.findViewById<Button>(R.id.edit_listing_submit_button)

        // Set up enable-editing button for each edit text field
        titleLayoutTi.setEndIconOnClickListener { titleTi.isEnabled = true }
        descriptionLayoutTi.setEndIconOnClickListener { descriptionTi.isEnabled = true }
        streetLayoutTi.setEndIconOnClickListener { streetTi.isEnabled = true }
        cityLayoutTi.setEndIconOnClickListener { cityTi.isEnabled = true }
        stateLayoutTi.setEndIconOnClickListener { stateTi.isEnabled = true }
        zipcodeLayoutTi.setEndIconOnClickListener { zipcodeTi.isEnabled = true }
        dimensionsLayoutTi.setEndIconOnClickListener { dimensionsTi.isEnabled = true }

        // Set the edit text fields to the current value of the listing's fields
        titleTi.setText(listing?.getTitle())
        descriptionTi.setText(listing?.getDescription())
        streetTi.setText(listing?.getAddressStreet())
        cityTi.setText(listing?.getAddressCity())
        stateTi.setText(listing?.getAddressState())
        zipcodeTi.setText(listing?.getAddressZip())
        dimensionsTi.setText(listing?.getDimensions())

        Glide.with(requireContext())
            .load(listing?.getImage()?.url)
            .placeholder(R.drawable.ic_profile)
            .transform(RoundedCorners(40))
            .into(listingImageIv)

        // TODO: Update new image stuff
        chooseNewPhotoButton.setOnClickListener {
            choosePhotoSelectorDialog()
        }
        listingImageIv.setOnClickListener{
            choosePhotoSelectorDialog()
        }

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
            if (dimensionsTi.isEnabled) {
                listing?.setDimensions(dimensionsTi.text.toString())
                dimensionsTi.isEnabled = false
            }
            if (photoFile != null) {
                listing?.setImage(ParseFile(photoFile))
            }

            saveChanges()
        }
    }

    fun choosePhotoSelectorDialog() {
        val PERMISSIONS = arrayOf<String>(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(
            requireActivity(),
            PERMISSIONS,
            1
        )
        MediaStore.Images.Media.TITLE
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(intent, SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    fun loadFromUri(photoUri: Uri): Bitmap? {
        var image: Bitmap? = null
        try {
            // check version of Android on device
            image = if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(requireActivity().contentResolver, photoUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                // support older versions of Android by using getBitmap
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
            val photoUri = data.data

            // Load the image located at photoUri into selectedImage
            val selectedImage = loadFromUri(photoUri!!)

            photoFile = FileUtils().getFileFromUri(requireContext(),photoUri)

            // Load the selected image into a preview
            requireView().findViewById<ImageView>(R.id.edit_listing_image_iv).setImageBitmap(selectedImage)
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